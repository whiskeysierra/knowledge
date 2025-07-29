---
tags:
  - Hexagonal Architecture
---

Properly slicing repository interfaces (our "ports" to
persistence) is a common snag in clean architectures like
Hexagonal. A frequent, problematic habit is to create a separate
repository interface for each database table. This often leads
to hidden coupling and defeats the purpose of adapter isolation.

## The Problem: Table-Per-Repository Coupling

Defining interfaces like `UserRepository` and `AddressRepository`
(one per table) often leaks database details into your core
application or, worse, couples underlying data concerns directly.

Consider common database operations:

* **JOINs / Cross-Table Dependencies:** If your domain needs a `User`
  with their `Address`, the application layer might be forced to
  call both `UserRepository` and `AddressRepository` then manually
  combine data. This breaks adapter isolation. Even more problematic,
  sometimes an adapter for one entity might implicitly rely on data
  from another table. For example, `AddressRepository` might need
  to check the `User` table directly to validate an address update
  if the address domain model somehow requires information (like
  user existence or a user-related field) that only the `User`
  table contains. This means `AddressRepository` is no longer
  isolated; it's implicitly coupled to the `User` table's structure
  and data concerns.

* **Transactions:** Consistent data often needs transactions spanning
  multiple tables (e.g., updating user and address atomically).
  Table-per-repository forces you to pass transaction objects around
  (leaking more database concerns) or relying on fragile ambient
  transactions. The core shouldn't care *how* persistence happens,
  only *what* data is persisted.

This means your "isolated" adapters for `User` and `Address` are
actually coupled, either through the application layer's
orchestration or through direct cross-table dependencies within
the adapters themselves, driven by database techniques like JOINs
and transactions.

## The Painful Symptom: Brittle Fakes

A big sign of this bad slicing is how hard it is to write good test
doubles (fakes) for your repository interfaces. If `UserRepository`
only has `saveUser(User user)` and `AddressRepository` has
`saveAddress(Address address)`, how do you fake an atomic update
across both?

It becomes much harder, more brittle, or even impossible to fake
transactional and join-based behavior accurately. Your test doubles
might not genuinely guarantee atomic updates across related entities,
making tests unreliable. This goes against good testing, including
hand-rolling fakes (see
[Hand-roll test doubles](../practices/hand-roll-test-doubles.md))
and using test contracts to ensure trust (see
[Test contract](../patterns/test-contract.md)).

## The Solution: Aggregate-Oriented Repositories

My preferred solution is to align repository interfaces with an
**aggregate root** concept. You don't need to buy into all of DDD here.

An aggregate is a cluster of domain objects treated as a single unit
for data changes. It defines a consistency boundary:

* **Read as a whole:** An aggregate (e.g., a `User` and their `Address`)
  is read as one complete unit via a single repository method, using
  JOINs if needed.
* **Written as a whole:** Changes to an aggregate (even if they touch
  multiple tables) are saved in one transaction via a single repository
  method.

So, an adapter's scope should be that of an aggregate. Instead of
`UserRepository` and `AddressRepository`, you'd have `UsersRepository`
(plural, a common convention to denote a repository for an aggregate
root like 'User').

### Bad Code: Table-Per-Repository

```kotlin
interface UserRepository {
    fun getById(userId: Guid): User
    fun save(user: User)
}

interface AddressRepository {
    fun getById(addressId: Guid): Address
    fun save(address: Address)
}
```

### Good Code: Aggregate-Oriented

```kotlin
interface UsersRepository {
    fun getById(userId: Guid): UserAggregate
    fun save(userAggregate: UserAggregate)
}
```

By doing this, you:

1.  **Enforce Adapter Isolation:** The database adapter truly knows how
    `User` and `Address` are stored and updated. The application core
    just asks for a `UserAggregate` or tells the repository to `save`.
2.  **Simplify Testing:** Fakes for `UsersRepository` can now accurately
    simulate atomic operations spanning multiple internal data
    structures, making tests more reliable and easier. The fake manages
    the `UserAggregate` in memory as a whole.
3.  **Reduce App Layer Logic:** The application service no longer
    orchestrates joins or manages transactions across multiple repository
    calls. The repository handles the complete persistence.

## Conclusion

Slicing repositories by database tables seems natural, but it creates
hidden coupling and makes testing harder. Shift to **aggregate-oriented**
repositories. This better upholds architectural isolation, simplifies
your app core, and makes writing robust test doubles much easier. It
makes your persistence layer a true adapter, hiding *how* data is
stored and only exposing *what* is relevant to your domain.
