---
title: Contract-style Unit Testing
---

🚧 Need to share test code when testing multiple implementations

Let's say we're dealing with the following interface...

```kotlin
interface UserRepository {
    fun create(user: User): User
    fun find(id: UserId): User?
}
```

... a couple of implementations:

- `InMemoryUserRepository` for fast unit tests
- `FileSystemUserRepository` for running locally
- `PostgresUserRepository` (or `JpaUserRepository`) for production

We're now left with the question of how to share tests between them.
The traditional answer is an abstract test class, e.g. `AbstractUserRepositoryTest`.

- Requires the use of inheritance
- Can't easily be split when it becomes too big
- 🚧 anything else?

> JUnit Jupiter allows `@Test`, `@RepeatedTest`, `@ParameterizedTest`, ... to be declared on interface default methods.
>
> [JUnit 5 User Guide: 2.13. Test Interfaces and Default Methods](https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-interfaces-and-default-methods)

```kotlin
interface UserRepositoryContract : TestFixtures {

    val unit: UserRepository

    @Test
    fun `finds user`() {
        val expected = unit.create(randomUser())
        val actual = unit.find(user.id)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `won't find absent user`() {
        val user = unit.create(randomUserId())
        assertThat(user).isNull
    }

    // more tests here...
}
```

```kotlin
internal class InMemoryUserRepositoryTest : UserRepositoryContract {
    override val unit = InMemoryUserRepository()
}
```

```kotlin
internal class FileSystemUserRepositoryTest : UserRepositoryContract {
    override val unit = FileSystemUserRepository(
            path = Files.createTempDirectory())
}
```

```kotlin
@SpringBootTest
internal class PostgresUserRepositoryTest : PostgresUserRepositoryContract {
    override val unit by lazy {
        // TODO does this work?!
    }
    // TODO docker container
    // TODO setup/teardown
}
```
