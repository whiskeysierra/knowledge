---
title:  Primitive Obsession
---

![](img/tool-1314070_1280.jpg)

Primitives have their use, but they shouldn't be our first choice.

When it comes to expressing things like identifiers or age we often tend to immediately jump towards primitives:

```kotlin
data class User(
        val id: Int,
        val age: Int,
        val name: String,
        val email: String)
```

There are actually several issues with that.

1. The compiler is no longer able to type-check. Possible errors, as outlined in the next code sample, are the result.
2. Validation is either often forgotten, or shared as separate methods, not together with the values themselves.

```kotlin
val id = 1337
val age = 36
val name = "D. Fault"
val email = "d.fault@example.org"
val user = User(age, id, email, name)
```

Instead of low level primitives like `Int`, `Boolean` and `String` one should strive towards typed, immutable value objects:

```kotlin
data class User(
        val id: UserId,
        val age: Age,
        val name: String,
        val email: Email)

class UserId(private val value: String) {
    // TODO equals, hashCode, toString
}

class Age(private val value: Int) : Comparable<Age> {
    init {
        require(value >= 0)
    }

    operator fun plus(age: Age) = Age(value + age.value)
    override fun compareTo(other: Age) = value.compareTo(other.value)
    // TODO equals, hashCode, toString
}

class Email(private val value: String) {
    init {
        EmailValidator().validate(value)
    }
    // TODO equals, hashCode, toString
}
```

## References

* [Refactoring Guru: Primitive Obsession](https://refactoring.guru/smells/primitive-obsession)
