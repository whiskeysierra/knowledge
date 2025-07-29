---
title: Test contract
tags:
  - Testing
  - Liskov substitution
---

## 💬 Intent

**Test contract** or _Contract-style unit test_ is a structural test design pattern that lets you define a contract for an interface and enforce
it for every concrete implementation.

## ☹️ Problem

Whenever you have an interface, you usually end up with multiple implementations.
While these implementations have distinct characteristics that make them different,
they should also share a common behavior as specified by the interface.
This specification is normally expressed just by the required method signatures and types.
But, of course, there are aspects of the specification you can't describe using the type system alone.

That's when tests come in.
A set of tests provides as an executable verification and acts as a guarantee that the system under test
actually adheres to the specification or _contract_.

The problem is though, that with multiple implementations, how do you ensure that they all adhere
to the same specification?

## 😊 Solution

The solution is to formulate the specification as a set of tests, coded against the interface itself,
in a single place, to be reused for each and every single implementation.

The approach that I like to use is based on test interfaces using default methods:

> JUnit Jupiter allows `@Test`, `@RepeatedTest`, ... to be declared on interface default methods.
>
> [JUnit 5 User Guide: 2.13. Test Interfaces and Default Methods][JUnit]

You basically create an interface with all the tests you need, defined as default methods.
That interface is the *test contract* and can now be easily re-used for different concrete tests for your
different implementations.

## Structure

```plantuml width="60%"
hide empty members

interface Thing {
    +perform(T): R
}

interface ThingContract

ThingContract -up-> Thing: specifies

class FakeThing implements Thing
class FakeThingTest implements ThingContract
FakeThingTest -up-> FakeThing : tests

class RealThing implements Thing
class RealThingTest implements ThingContract
RealThingTest -up-> RealThing : tests
```

## Pseudocode

This example uses a `UserRepository` interface and two implementations, 
`InMemoryUserRepository` (a [hand-rolled](../practices/hand-roll-test-doubles.md) fake) and
`PostgresUserRepository`:

```plantuml
hide empty members

interface UserRepository {
    +create(User): User
    +find(UserId): User?
}

interface UserRepositoryContract

class InMemoryUserRepository implements UserRepository
class InMemoryUserRepositoryTest implements UserRepositoryContract
InMemoryUserRepositoryTest -up-> InMemoryUserRepository : tests

class PostgresUserRepository implements UserRepository
class PostgresUserRepositoryTest implements UserRepositoryContract
PostgresUserRepositoryTest -up-> PostgresUserRepository : tests
```

```kotlin
{% include "../../src/test/kotlin/patterns/contract/UserRepositoryContract.kt" %}
```

Note that there is no implementation specific code in here.
Every test is defined using the `UserRepository` interface.
In addition, there is no setup or teardown logic.


!!! Tests

    === "InMemoryUserRepositoryTest"

        ```kotlin
        {% include "../../src/test/kotlin/patterns/contract/InMemoryUserRepositoryTest.kt" %}
        ```

    === "PostgresUserRepository"

        ```kotlin
        {% include "../../src/test/kotlin/patterns/contract/PostgresUserRepositoryTest.kt" %}
        ```


### Transfer of confidence

You have two implementations,
one for production that is too slow for tests
and one that isn't for production but is fast.

The test for your production variant gives you confidence for production.
Since a test contract executes and verifies the same tests against your fake,
you can now be confident that you can use the fake whenever you'd usually need the production variant.

The test contract allows you to transfer the trust from one implementation to another.
In other words, it's a way to verify the [Liskov substitution principle][Liskov].

## Alternatives

There are some options you could use:

### Abstract test class

While it works, it's not ideal. You can only have one base class and e.g. some database testing
libraries/frameworks require you to use class inheritance which is then no longer possible.
Also, I'm not a big fan of inheritance and favor [composition](../composition.md) whenever I can.

### `@ParameterizedTest`

... using instances of all implementations as the parameters.
Technically doable, but it would also require to have all setup and teardown logic for all implementations
in a single test - which will quickly become a maintenance nightmare.

Both of these also share the downside that one can't easily split the tests up into smaller test classes,
because one would need to repeat itself - a lot.

## References

- [JUnit 5 User Guide: 2.13. Test Interfaces and Default Methods][JUnit]
- [Liskov substitution principle][Liskov]

[Liskov]: https://en.wikipedia.org/wiki/Liskov_substitution_principle
[JUnit]: https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-interfaces-and-default-methods
