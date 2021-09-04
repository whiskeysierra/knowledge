---
title:  Composition
---

![](img/puzzle-1713170_1280.jpg)

> *noun*: **composition**, the action of putting things together; formation or construction.

## Composition everywhere

What I like about composition is, that it applies to all scales:

* Functions and methods
* Classes
* Components
* Applications
* Systems

The idea is always the same.
I try to break up (decompose) a problem into smaller sub-problems and solve them individually (*Divide and conquer*).
Then I'm taking those pieces and compose them to form a solution to the overall problem.

What I get in return is:

* **Focus**  
  I can take one component, work with it and not have to burden myself with the rest.
* **Reusability**  
  I can reuse components whenever I have the same problem again.
* **Isolation**  
  Clean interfaces between components allow me to safely refactor within a component, without the risk of breaking the
  rest.

## Inheritance

Personally, I'm not just *favoring* composition over inheritance.
I use composition (almost) exclusively.
The one and only use case for inheritance that I see is to interact with frameworks (that didn't get the memo about inheritance).

See also [Effective Java: Favor Composition Over Inheritance](https://dev.to/kylec32/effective-java-tuesday-favor-composition-over-inheritance-4ph5).

## N+1

There is a consequence of applying composition to design.
I often call it the *N+1* situation, and it goes like this:

Let's say I start off with one component.
Now I start understanding the requirements and responsibilities.
Assuming I end up with three distinct responsibilities, ergo three different components.

In order to achieve the overall goal I don't need three pieces, I need one whole.
That means I need something that puts the three pieces together, i.e. the integration part.
Sometimes, when I have more than just three components, I might even need multiple integrations.

I often try to manage expectations by stating early on, that we should expect N+1 components in the end.

It's also important to push back on the notion that a design with N (+1) components is more complex than the original design with just one.
Complexity is not determined by the number of parts, but the number of connections.
The original one-size-fits-all component had those connections already, but they were implicit and hidden.
Yet they still hurt, because I need to understand them in order to successfully work with said component.

## References

* [Composition over inheritance](https://en.wikipedia.org/wiki/Composition_over_inheritance)
* [Rich Hickey - Simple Made Easy](https://www.youtube.com/watch?v=oytL881p-nQ)
