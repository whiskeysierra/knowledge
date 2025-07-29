---
tags:
  - Testing
  - Mocks
  - Least Astonishment
---

> _"I'm long past on record that I think the use of auto-mockers [...] is bad policy."_  
> — [GeePaw Hill](https://www.geepawhill.org/2021/07/13/on-not-using-mocking-frameworks/), July 13th, 2021

Mocking libraries[^1] are everywhere these days, promising easy test setup and readable code.
But honestly, it's almost always way better to just roll your own test doubles.
Especially if you care about clean, maintainable tests.
The only real exception is during legacy migrations, and even then, mocking libraries should be considered a temporary crutch, not a long-term solution.
Let's talk about why explicit code virtually always beats the magic of mocking frameworks.

## What are Test Doubles Anyway?

[Test doubles](https://martinfowler.com/bliki/TestDouble.html) are objects that stand in for real dependencies during testing.
They allow you to isolate the unit under test, controlling its interactions with other parts of the system and ensuring predictable test outcomes.

Before using test doubles, consider whether you can use the real dependency; if it's cheap, fast, and safe, using the real thing is preferable.
This approach aligns with the concept of [*sociable unit tests*](https://martinfowler.com/bliki/UnitTest.html#SolitaryOrSociable),
which verify the behavior of multiple components in collaboration.
Test doubles should only be used when using the real dependency is impractical or problematic.

Here are the common types (roughly ordered by my personal preference, from most to least preferred in most situations):

*   **Fakes**

    Fakes are working implementations of a dependency, but they are simplified for testing purposes.
    Examples include in-memory databases or simplified versions of external services.
    They provide a functional but lightweight alternative to the real dependency.

*   **Stubs**

    Stubs provide canned answers to method calls ([indirect input](http://xunitpatterns.com/indirect%20input.html)).
    They simplify test setup by returning predefined values, avoiding complex setup of real dependencies.

*   **Spies**

    Spies are a bit more nuanced than stubs.
    They "spy" on the interactions with the real object (or a partial mock) for [indirect output](http://xunitpatterns.com/indirect%20output.html).
    There are two main types of spies:

    *   **Interaction-based**

        These are the type of spies commonly implemented by mocking libraries.
        They focus on verifying *how* a dependency was used.
        They track method calls, the arguments passed to those methods, and the order of calls.

    *   **State-based**

        These spies focus on observing the *state* changes of the object being spied on.
        Instead of verifying specific method calls, they verify that the object's internal state has changed in the expected way as a result of the interaction.

*   **Dummies**

    These are objects passed as arguments but are never actually used within the method being tested.
    They are simply placeholders to satisfy method signatures.

*   **Mocks**

    Mocks are pre-programmed with expectations about how they will be called.
    If these expectations are not met during the test, the test fails.
    Mocks are used to verify interactions and enforce specific behavior.

## The Problem with Mocking Libraries

While mocking libraries offer convenient test double creation and reduced boilerplate, they also present noteworthy drawbacks:

*   **Mocks are not aware of the interface contract**

    Mocks only know about the specific methods and behaviors you explicitly define in each test.
    This means there's no compile-time or runtime check to ensure these mock setups adhere to the actual interface *contract*.
    This lack of enforcement creates a significant risk of introducing subtle differences between the mock and the real class that can go undetected.

    The term "_contract_", in this context, refers to both the syntax (method signatures) and semantics (intended behavior) of an interface.

*   **Tight Coupling to Implementation**

    Overusing mocks ties tests to internal implementation details.
    Refactoring internal code, even without changing the public interface, can break numerous tests, hindering refactoring efforts.

*   **Testing Interactions, Not Behavior**

    Mocking often focuses on verifying interactions (how dependencies are used) rather than behavior (what the code achieves).
    This leads to brittle tests that don't effectively validate the intended functionality.

*   **"Magic" and Increased Complexity**

    Mocking libraries rely on complex mechanisms and introduce their own APIs, creating a "magic" layer that obscures test behavior and increases the learning curve.
    This makes tests harder to understand, debug, and maintain, especially for newcomers.

## Why Hand-Rolling is Often Better

Hand-rolled test doubles offer several compelling advantages over mocking libraries:

*   **Direct Interface Design Feedback**

    Creating hand-rolled test doubles provides immediate feedback on your interface design.
    If a test double becomes complex, it suggests the interface it's doubling is too complex, prompting you to apply principles like the Interface Segregation Principle for cleaner code.

*   **Testable Test Doubles**

    Hand-rolled test doubles, especially fakes, are regular classes that can be independently tested.
    This ensures their correctness and enhances the reliability of your system's tests, especially when fakes contain complex logic.
    Check out [Test contract](../patterns/test-contract.md) for a good way to test fakes.

*   **Explicit and Understandable Code**

    Hand-rolled doubles are written directly in your test code, making them easy to understand, debug, and maintain.
    Their behavior is entirely explicit, avoiding the "magic" of mocking frameworks.

*   **Reduced Coupling to Implementation**

    By focusing on essential behavior, hand-rolled doubles minimize coupling to implementation details.
    This makes your tests more resilient to refactoring, as internal changes are less likely to break them.

*   **Tests Focused on Behavior**

    Hand-rolling encourages you to focus on the inputs and outputs relevant to the test, promoting behavior-driven testing.
    This results in more expressive and maintainable tests that focus on *what* the code does, not *how*.

*   **Simplified Debugging**

    When a test with hand-rolled doubles fails, debugging is easier because the test double's behavior is directly visible in the code.
    You avoid the need to delve into complex mocking framework internals.

## Examples

This section demonstrates hand-rolled test doubles in Kotlin, showcasing each of the five main types:

=== "Fake"

    !!! tip

        Fakes are particularly well-suited for implementing repository interfaces because they provide simplified,
        in-memory implementations that mimic the behavior of a real database or external service, making tests fast
        and predictable.

    ```kotlin
    {% include "../../src/main/kotlin/practices/handroll/fake/UserRepository.kt" %}
    ```

    ```kotlin
    {% include "../../src/test/kotlin/practices/handroll/fake/InMemoryUserRepository.kt" %}
    ```

    ```kotlin
    {% include "../../src/test/kotlin/practices/handroll/fake/UserServiceTest.kt" %}
    ```

=== "Stub"

    !!! tip

        This example uses the [Configurable Responses](https://www.jamesshore.com/v2/projects/nullables/testing-without-mocks#configurable-responses)
        pattern as implemented by [whiskeysierra/test-doubles](https://github.com/whiskeysierra/test-doubles).

    ```kotlin
    {% include "../../src/main/kotlin/practices/handroll/stub/DataProvider.kt" %}
    ```

    ```kotlin
    {% include "../../src/test/kotlin/practices/handroll/stub/StubDataProvider.kt" %}
    ```

    ```kotlin
    {% include "../../src/test/kotlin/practices/handroll/stub/DataProcessorTest.kt" %}
    ```

=== "Spies"

    !!! tip

        This example uses the [Output Tracking](https://www.jamesshore.com/v2/projects/nullables/testing-without-mocks#output-tracking)
        pattern as implemented by [whiskeysierra/test-doubles](https://github.com/whiskeysierra/test-doubles).

    ```kotlin
    {% include "../../src/main/kotlin/practices/handroll/spy/EmailService.kt" %}
    ```

    ```kotlin
    {% include "../../src/test/kotlin/practices/handroll/spy/SpyEmailService.kt" %}
    ```

    ```kotlin
    {% include "../../src/test/kotlin/practices/handroll/spy/UserNotifierTest.kt" %}
    ```

=== "Dummy"

    !!! warning

        Only hand-roll a dummy if a suitable default/noop implementation (like a [Null Object](https://sourcemaking.com/design_patterns/null_object)) doesn't already exist for the required interface.

    ```kotlin
    {% include "../../src/main/kotlin/practices/handroll/dummy/Currency.kt" %}
    {% include "../../src/main/kotlin/practices/handroll/dummy/Money.kt" %}
    {% include "../../src/main/kotlin/practices/handroll/dummy/CurrencyConverter.kt" %}
    {% include "../../src/main/kotlin/practices/handroll/dummy/EuroConverter.kt" %}
    ```

    ```kotlin
    {% include "../../src/test/kotlin/practices/handroll/dummy/DummyCurrency.kt" %}
    ```

    ```kotlin
    {% include "../../src/test/kotlin/practices/handroll/dummy/EuroConverterTest.kt" %}
    ```

=== "Mock"

    !!! warning "A Word of Caution"

        While hand-rolling mocks is technically possible (as shown below), it’s often less ideal than using simpler types of test doubles.
        
        Hand-rolled mocks present two key challenges.
        First, the need to pre-program and verify expectations increases the complexity of the mock itself, making it harder to write, understand, and maintain.
        Second, and this applies to mocks in general, hiding expectations and verifications within the mock makes the tests themselves less expressive and readable, as they contain less visible evidence (assertions).
        Consider using simpler test doubles like stubs, spies, or fakes before resorting to hand-rolled mocks.

    ```kotlin
    {% include "../../src/main/kotlin/practices/handroll/mock/PaymentGateway.kt" %}
    ```

    ```kotlin
    {% include "../../src/test/kotlin/practices/handroll/mock/MockPaymentGateway.kt" %}
    ```

    ```kotlin
    {% include "../../src/test/kotlin/practices/handroll/mock/OrderProcessorTest.kt" %}
    ```

## Conclusion

Mocking libraries have their place, especially for complex, existing interfaces or external dependencies you can't
easily replicate
But for many cases, hand-rolled test doubles are a much better choice.
They lead to simpler, more maintainable tests and give you valuable feedback on your design.
So, before reaching for a mocking library, take a moment to consider if a simple hand-rolled double might do the trick.
You might be surprised how often it's the right answer.

## _Testing Without Mocks_

As astute readers may have noticed, this discussion draws on several patterns from James Shore's [*_Testing Without Mocks_*](https://www.jamesshore.com/v2/projects/nullables/testing-without-mocks) article.
It is packed with useful testing tricks and techniques, from test design to dependency management, offering practical ways to improve test clarity, maintainability, and effectiveness.
Even if you're not buying into the whole premise, checking out these patterns can seriously improve your test design.

## References

* [On not using Mocking Frameworks](https://www.geepawhill.org/2021/07/13/on-not-using-mocking-frameworks/)
* [Test Double](https://martinfowler.com/bliki/TestDouble.html)
* [Mocks, Fakes, Stubs and Dummies](http://xunitpatterns.com/Mocks,%20Fakes,%20Stubs%20and%20Dummies.html)
* [Configurable Responses](https://www.jamesshore.com/v2/projects/nullables/testing-without-mocks#configurable-responses)
* [Output Tracking](https://www.jamesshore.com/v2/projects/nullables/testing-without-mocks#output-tracking)

[^1]: For example [Mockito](https://site.mockito.org/) or [MockK](https://mockk.io/)
