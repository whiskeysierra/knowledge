## 💬 Intent

See [Refactoring Guru: Composite](https://refactoring.guru/design-patterns/composite)

## ☹️ Problem

I often stumble upon the following problem.
Starting off with a class that accepts a callback of some kind.

```kotlin
{% include "../../src/main/kotlin/patterns/composite/v1/UserService.kt" start="start" %}
```

Could be a listener, notifier or anything along those lines:

```kotlin
{% include "../../src/main/kotlin/patterns/composite/v1/Listener.kt" start="start" %}
```

More often than not, I have the need to add support for multiple of callbacks.
The straightforward and somewhat compelling implementation looks like this: 

```kotlin
{% include "../../src/main/kotlin/patterns/composite/v2/UserService.kt" start="start" %}
```

It does have a few aspects in its favor:

* ✅ Easy to understand
* ✅ Easy to implement

But it comes with a bunch of problems as well:

* ❌ Violates the [Single-responsibility principle](https://en.wikipedia.org/wiki/Single-responsibility_principle)
    * `UserService` should only change 
* ❌ Violates the [Open-closed principle](https://en.wikipedia.org/wiki/Open%E2%80%93closed_principle)
    * `UserService` should not be modified to extend its behavior 

In addition, the proposed implementation base on `Collection#forEach` is too simplistic.
A proper implementation should also take the following into consideration,
just to name a few:

* ❌ Error handling  
  If one listener fails, the remaining ones won't be executed.
* ❌ Concurrency  
  Running listeners in parallel.
* ❌ Asynchrony  
  Executing listeners asynchronously, not waiting for them to finish.
* ❌ Dynamic listener de/registration  
  Allow adding and removing listeners at runtime.
  This is not totally trivial in a multi-threaded environments.

Adding support for any of them is not rocket science, but it would increase
the complexity of `UserService` quite a bit.

## 😊 Solution

## Structure

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

## Pseudocode


```plantuml
hide empty members

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

The ideal solution addresses all concerns above.

```kotlin
{% include "../../src/main/kotlin/patterns/composite/v3/UserService.kt" start="start" %}
```

The `UserService` didn't need to change compared to the original design.
No principle would be violated:

* ✅ [Single-responsibility principle](https://en.wikipedia.org/wiki/Single-responsibility_principle)
* ✅ [Open-closed principle](https://en.wikipedia.org/wiki/Open%E2%80%93closed_principle)

We create a new class instead:

```kotlin
{% include "../../src/main/kotlin/patterns/composite/v3/CompositeListener.kt" start="start" %}
```

The other concerns could (as needed) all addressed with separate classes
using the *Decorator* pattern:

* ✅ Error handling  
  ```kotlin
  {% include "../../src/main/kotlin/patterns/composite/v3/ErrorHandlingListener.kt" start="start" %}
  ```  
  ```kotlin
  {% include "../../src/main/kotlin/patterns/composite/v3/ErrorHandler.kt" start="start" %}
  ```
* ✅ Concurrency
  ```kotlin
  {% include "../../src/main/kotlin/patterns/composite/v3/ConcurrentCompositeListener.kt" start="start" %}
  ```
* ✅ Asynchrony
  ```kotlin
  {% include "../../src/main/kotlin/patterns/composite/v3/AsyncListener.kt" start="start" %}
  ```
* ✅ Dynamic listener de/registration  
  Just pass a `CopyOnWriteArrayList` to `CompositeListener` and modify it as needed.

```kotlin
{% include "../../src/main/kotlin/patterns/composite/v3/Usage.kt" start="start" %}
```

## 💡Applicability

## 📝 How to implement

## ⚖︎ Pros and Cons

## References

* [Refactoring Guru: Composite](https://refactoring.guru/design-patterns/composite)
* [SourceMaking: Composite](https://sourcemaking.com/design_patterns/composite)
