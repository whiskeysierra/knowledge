---
tags:
  - Hexagonal Architecture
---

In hexagonal architecture, adapters translate between our domain
language and the language of a specific technology — a database,
a message broker, an HTTP API. The whole point of having adapters
is **isolation**: the core doesn't know or care about the
technology behind an adapter, and adapters don't know about each
other.

Creating well-architected adapters that truly live up to that
promise isn't easy. There are many subtle ways to introduce
dependencies between adapters, and not all of them are obvious.
It helps to think about them in terms of the *kind* of dependency
— from the straightforward ones that a compiler can catch to the
insidious ones that hide in shared external state.

## Direct Dependencies

The most straightforward violation is when one adapter directly
depends on another — it imports its types, calls its methods, or
receives it as a constructor parameter.

```kotlin
// Don't do this
class EmailNotificationAdapter(
    private val users: UsersRepository, // adapter depending on adapter
    private val smtp: SmtpClient,
) : NotificationPort {
    override fun notify(userId: UserId, message: String) {
        val user = users.getById(userId)
        smtp.send(user.email, message)
    }
}
```

The `EmailNotificationAdapter` depends on the persistence
adapter. It can't be tested, deployed, or replaced independently.
If the users repository changes its interface, the email adapter
breaks.

This kind of dependency is easy to spot. The compiler tells you.
Tools like [ArchUnit](https://www.archunit.org/) can enforce it
automatically. And the fix is simple: let the application core
do the coordination. An adapter should receive everything it
needs.

```kotlin
class EmailNotificationAdapter(
    private val smtp: SmtpClient,
) : NotificationPort {
    override fun notify(recipient: Email, message: String) {
        smtp.send(recipient, message)
    }
}
```

```kotlin
class NotifyUserUseCase(
    private val users: UsersRepository,
    private val notifications: NotificationPort,
) {
    fun execute(command: NotifyUser) {
        val user = users.getById(command.userId)
        notifications.notify(user.email, command.message)
    }
}
```

The application core reads the user and passes the email to the
notification adapter. Each adapter only knows about its own
technology. The coordination — which is business logic ("look up
the user, then notify them") — stays where it belongs: in the
domain.

## Shared State

The more insidious kind of coupling. Two adapters don't reference
each other at all — there's no import, no type dependency, no
ArchUnit violation. But they both interact with the same external
stateful resource in an implicitly coordinated way. They're
coupled, just not visibly.

An important distinction: two adapters using the same
*technology* is perfectly fine. You can have two adapters that
both talk to Azure Blob Storage or both use PostgreSQL. The
problem arises when they interact with the same *state* — the
same blobs, the same database tables, the same queue messages.

### Shared Tables and JOINs

A common symptom is slicing persistence adapters along database
tables rather than domain aggregates. If you have a
`UserRepository` and an `AddressRepository` for what is
conceptually a single `User` aggregate, the adapters look
independent but they're not. One adapter's queries might JOIN
against the other's tables. A schema migration for one silently
breaks the other. The application core is forced to coordinate
what should be a single persistence concern.

I've written about this in detail in
[Aggregates, Not Tables](aggregates-not-tables.md). The fix is
to align repository boundaries with aggregate boundaries, not
table boundaries.

### Shared Transactions

Once persistence is split into too many adapters, the next
temptation is to glue them back together with shared database
transactions.

```kotlin
class CreateOrderUseCase(
    private val orders: OrdersRepository,
    private val inventory: InventoryRepository,
    private val transactionManager: TransactionManager,
) {
    fun execute(command: CreateOrder) {
        transactionManager.executeInTransaction {
            val items = inventory.reserve(command.items)
            orders.save(Order(command.customerId, items))
        }
    }
}
```

The `TransactionManager` is a database concern — it's about how
a relational database ensures consistency. By injecting it into
the application core, you've leaked a technology detail. Worse,
you've implicitly coupled the `orders` and `inventory` adapters:
they *must* share the same database (and often the same
connection) for this to work. Swap one of them for a different
storage technology, and the transaction silently stops working.

The fix is the same as for shared tables: if two things need to
be transactionally consistent, they belong to the same aggregate
and therefore the same adapter.

```kotlin
class CreateOrderUseCase(
    private val orders: OrdersRepository,
) {
    fun execute(command: CreateOrder) {
        orders.save(Order(command.customerId, command.items))
    }
}
```

The `OrdersRepository` adapter internally handles reserving
inventory and saving the order in one transaction. The
application core doesn't know or care that transactions exist.

If orders and inventory genuinely belong to different aggregates
(or even different bounded contexts), then you don't actually
*want* a transaction spanning both. You want eventual consistency
instead.

### Other Shared Resources

Tables and transactions are the most common examples, but the
pattern applies to any external stateful resource: two adapters
reading and writing the same blobs in storage, the same files on
disk, or the same messages in a queue. In all these cases, the
adapters appear independent in the code but are tightly coupled
through shared external state. Changing one's data model or
access patterns can silently break the other.

## Conclusion

Adapter isolation sounds simple — "just keep adapters
independent" — but the violations range from obvious to nearly
invisible. Direct dependencies between adapters are easy to catch
with a compiler or architecture tests. Shared state dependencies
are far harder to spot: adapters that look perfectly isolated in
the code but are quietly coupled through the same database
tables, transactions, blobs, or files.

The guiding principle is always the same: each adapter owns its
private slice of external state and translates between a single
technology and the domain. All coordination belongs in the
application core. When you find yourself reaching across adapter
boundaries — whether through imports or shared state — it's a
sign that either your aggregate boundaries are wrong or your
application layer isn't doing its job.

## References

* [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
* [ArchUnit](https://www.archunit.org/)
