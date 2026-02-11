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
promise isn't easy. There are many subtle ways to violate adapter
isolation, and not all of them are obvious. Some introduce
dependencies between adapters. Others let technology leak out of
an adapter into the domain. It helps to think about them in terms
of the *kind* of violation — from the straightforward ones that a
compiler can catch to the insidious ones that hide in shared
external state or in your domain model's annotations.

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

## Technology Leaking into the Domain

The previous sections focused on coupling *between* adapters. But
there's another isolation violation that's at least as common and
arguably more damaging: letting an adapter's technology leak
*into the domain*.

The most pervasive example is ORMs. In hexagonal architecture,
a repository adapter translates between domain objects and
whatever storage technology it uses. An ORM is one possible tool
for that translation. The problem is that most ORMs don't stay
inside the adapter — they want to own your model.

### Annotations on the Domain Model

With JPA, the typical approach is to annotate your domain
entities directly:

```java
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineItem> items;

    // ...
}
```

This `Order` *looks* like a domain model, but it's actually a
persistence model. It knows about tables, columns, join
strategies, fetch modes, and cascade behavior. The domain is
now coupled to JPA — and transitively to a relational database.
You can't use this class in a context where JPA isn't on the
classpath. You can't test domain logic without at least stubbing
out the persistence framework. And every change to your storage
strategy requires touching your domain model.

### Generated Models as Domain Models

The same problem appears from the opposite direction when using
code generators like jOOQ or GraphQL codegen. Instead of
annotating domain classes, you generate data classes from your
schema and then use them as your domain model.

```kotlin
// Generated by jOOQ from the 'orders' table
val order = dsl.selectFrom(ORDERS)
    .where(ORDERS.ID.eq(orderId))
    .fetchOne()

// Now passed around as the "domain" object
orderService.process(order) // OrderRecord, not a domain type
```

The domain model is now *literally* the data model. The shape of
your domain is dictated by your database schema. Adding a domain
concept that doesn't map to a column is awkward. Changing the
schema forces changes through every layer. You've inverted the
dependency: instead of the adapter depending on the domain, the
domain's shape is determined by the adapter's technology.

### Keeping ORMs Inside the Adapter

ORMs can be used — as an implementation detail of a repository
adapter. The key is that the ORM's models, annotations, and
configuration stay inside the adapter. The adapter translates
between the ORM's world and the domain's world, just as it
would translate between raw SQL results and domain objects.

```kotlin
// Domain model — no persistence annotations, no framework dependency
class Order(
    val id: OrderId,
    val customer: CustomerId,
    val items: List<LineItem>,
)
```

```kotlin
// Inside the adapter — JPA entity, never leaves this package
@Entity
@Table(name = "orders")
internal class OrderEntity(
    @Id val id: UUID,
    @ManyToOne @JoinColumn(name = "customer_id")
    val customer: CustomerEntity,
    @OneToMany(cascade = [CascadeType.ALL])
    val items: List<LineItemEntity>,
)
```

```kotlin
// The adapter translates between the two
class JpaOrdersRepository(
    private val jpa: JpaOrderEntityRepository,
) : OrdersRepository {
    override fun findById(id: OrderId): Order? =
        jpa.findById(id.value)?.toDomain()

    override fun save(order: Order) =
        jpa.save(order.toEntity())
}
```

This is strictly more work than annotating the domain model
directly. You have two model hierarchies and mapping code between
them. Whether that trade-off is worth it depends on your context —
but the architecture is clear: the ORM is a detail of *how* the
adapter persists things, and that detail doesn't leak into the
domain.

## Conclusion

Adapter isolation sounds simple — "just keep adapters
independent" — but the violations range from obvious to nearly
invisible. Direct dependencies between adapters are easy to catch
with a compiler or architecture tests. Shared state dependencies
are far harder to spot: adapters that look perfectly isolated in
the code but are quietly coupled through the same database
tables, transactions, blobs, or files. And technology leaking
into the domain — through ORM annotations, generated models, or
framework types — is so normalized that many teams don't even
recognize it as a violation.

The guiding principle is always the same: each adapter owns its
private slice of external state and translates between a single
technology and the domain. Technology stays inside the adapter.
All coordination belongs in the application core. When you find
yourself reaching across adapter boundaries — whether through
imports, shared state, or annotations on your domain model —
it's a sign that either your aggregate boundaries are wrong, your
application layer isn't doing its job, or your adapter's
technology has escaped its enclosure.

## References

* [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
* [ArchUnit](https://www.archunit.org/)
