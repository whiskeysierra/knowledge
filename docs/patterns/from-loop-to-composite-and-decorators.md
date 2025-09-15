---
title: From Loop to Composite and Decorators
tags:
  - Single responsibility
  - Open/Closed
---
## Introduction

You start with a class needing a callback.
Then, that one callback often grows into many.
This common progression can subtly lead to violations of
core design principles and accumulate hidden complexity
in your codebase.

## The Problem

Let's imagine you have a `UserService` that needs to
notify a single `Listener` about user-related events:

```kotlin
{% include "../../src/main/kotlin/patterns/composite/v1/UserService.kt" %}
```

Here's what that `Listener` interface looks like:

```kotlin
{% include "../../src/main/kotlin/patterns/composite/v1/Listener.kt" %}
```

More often than not, you'll eventually need to add support
for multiple callbacks.
The straightforward and often tempting approach looks like this:

```kotlin
{% include "../../src/main/kotlin/patterns/composite/v2/UserService.kt" %}
```

While this seems simple and easy to understand at first,
it comes with significant problems:

* **Violates the Single-Responsibility Principle (SRP):**
  Your `UserService` now has two distinct reasons to change.
  It's responsible for its core user management logic and
  for managing and iterating through a collection of listeners.
  According to SRP, it should only change due to modifications
  in its user management responsibilities.
* **Violates the Open-Closed Principle (OCP):**
  If you need to change *how* listeners are invoked—for example,
  by adding error handling, running them concurrently, or
  executing them asynchronously—you'll have to modify
  `UserService` itself.
  Its behavior isn't open for extension without requiring changes
  to its existing code.

Beyond these principle violations, a simple `Collection#forEach`
loop is often too naive for real-world scenarios.
A robust implementation would quickly bloat `UserService` with
complex logic for:

* **Error Handling:**
  If one listener throws an exception, it might prevent
  subsequent listeners from running.
* **Concurrency:**
  Managing listeners that need to run in parallel.
* **Asynchrony:**
  Executing listeners without waiting for them to finish.
* **Dynamic Listener De/Registration:**
  Allowing listeners to be added or removed at runtime, which can be
  particularly tricky in multi-threaded environments.

Adding support for any of them isn't rocket science, but it would
increase the complexity of `UserService` quite a bit.

---

## The Solution

The ideal solution allows your `UserService` to remain focused
on its single responsibility, treating a collection of listeners
as a single unit.
This is precisely where the **Composite pattern** shines.

Here's the conceptual structure of the Composite pattern for
this scenario:

```plantuml
hide empty members

interface Component
Component : +run()
class CompositeComponent {
  -elements: Collection<Component>
  +run()
}
class SomeComponent
SomeComponent : +run()

Client --> Component: use
Component o-- CompositeComponent
CompositeComponent .up.|> Component
SomeComponent .up.|> Component
```

Applied to our `Listener` example, the conceptual design looks
like this:

```plantuml
hide empty empty members

interface Listener {
  +run()
}
class CompositeListener {
  -listeners: Collection<Listener>
  +run()
}
class LoggingListener {
  +run()
}
class MetricsListener {
  +run()
}

UserService --> Listener: use
Listener o-- CompositeListener
CompositeListener .up.|> Listener
LoggingListener .up.|> Listener
MetricsListener .up.|> Listener
```

With this approach, your `UserService` didn't need to change
at all from its original single-callback design:

```kotlin
{% include "../../src/main/kotlin/patterns/composite/v3/UserService.kt" %}
```

Both the **Single-Responsibility Principle** and the
**Open-Closed Principle** are now fully respected.
The responsibility of managing multiple listeners is cleanly
moved into a new class:

```kotlin
{% include "../../src/main/kotlin/patterns/composite/v3/CompositeListener.kt" %}
```

This `CompositeListener` can be treated exactly like any other
single `Listener`.
Crucially, other cross-cutting concerns can now be addressed by
**decorating** the `Listener` or `CompositeListener` with
separate, dedicated classes.
This approach maintains strict isolation and extensibility:

* **Error Handling**
```kotlin
{% include "../../src/main/kotlin/patterns/composite/v3/ErrorHandlingListener.kt" %}
```

```kotlin
{% include "../../src/main/kotlin/patterns/composite/v3/ErrorHandler.kt" %}
```

* **Concurrency**
```kotlin
{% include "../../src/main/kotlin/patterns/composite/v3/ConcurrentCompositeListener.kt" %}
```

* **Asynchrony**
```kotlin
{% include "../../src/main/kotlin/patterns/composite/v3/AsyncListener.kt" %}
```

* **Dynamic Listener De/Registration**
  You can simply pass a `CopyOnWriteArrayList` to `CompositeListener`
  and modify it as needed for thread-safe dynamic updates.

Here's an example of how you might assemble and use these
listeners in your application:

```kotlin
{% include "../../src/main/kotlin/patterns/composite/v3/Usage.kt" %}
```

---

## Trade-offs

This pattern, like any other, comes with its own set of
advantages and disadvantages.

### Advantages:

* **Principle Adherence:**
  Directly supports SRP and OCP by clearly separating concerns.
* **Extensibility:**
  Easily add new listener behaviors (e.g., logging, metrics, security)
  by composing them with decorators.
* **Maintainability:**
  Changes to listener management logic do not require modifying
  your core `UserService`.
* **Clearer Responsibilities:**
  `UserService` can focus solely on its domain task.
* **Treat Many as One:**
  Clients interact with a group of objects in the same way they
  interact with a single object.
* **Explicit vs. Implicit Complexity:**
  The pattern takes the inherent complexity of managing multiple,
  often interdependent, cross-cutting concerns (like error handling
  and concurrency) and makes it explicit, isolated, and therefore
  more manageable and understandable, rather than interweaving it
  into a monolithic loop.

### Disadvantages:

* **Increased Number of Explicit Components:**
  The pattern necessitates creating more separate classes (e.g.,
  `CompositeListener`, `ErrorHandlingListener`, `AsyncListener`)
  compared to a single method with interwoven logic.
  While this improves clarity, it does increase the raw count
  of files or types.
* **Increased Indirection:**
  Executing a listener's logic now involves passing through the
  `CompositeListener` and potentially several Decorators.
  This added indirection, while beneficial for flexibility, can make
  debugging call stacks slightly longer or less intuitive for
  those unfamiliar with the pattern's flow.
* **Initial Learning Investment:**
  For developers new to design patterns like Composite and Decorator,
  there's an upfront investment required to understand the underlying
  concepts and how to apply them effectively.

---

## Conclusion

The Composite pattern offers a robust and maintainable solution for
managing multiple callbacks in a way that respects fundamental
design principles.
By externalizing the complexities of listener management and invocation,
you keep your core services clean, focused, and adaptable to future
requirements.

---

## References

* [Refactoring Guru: Composite](https://refactoring.guru/design-patterns/composite)
* [SourceMaking: Composite](https://sourcemaking.com/design_patterns/composite)
* [Domain-Oriented Observability](https://martinfowler.com/articles/domain-oriented-observability.html)