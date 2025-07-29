# Two Sides to Every Coin

![](../img/coin-8839127_1280.jpg)

This is my evolving collection of notes on the inherent trade-offs
in software engineering. For pretty much every "good" principle or
practice, there's often a challenging flip side that comes with it.
Understanding these dualities helps me make better design choices and
avoid getting stuck on one-sided thinking.

This is very much a work in progress. I'll add to it whenever I
stumble upon another good example.

---

## The Examples

### Reuse ⇌ Coupling

**Reuse** is awesome for efficiency and consistency.
Why build it again if you don't have to?
But it always brings **coupling** with it.
When you reuse code, you're now tied to it.
Changes in the reused bit can unexpectedly mess with everything
that uses it, making independent updates harder.
The trick is to manage that coupling, keeping it loose and obvious.

### Divide & Conquer ⇌ Compartments / Silos

The **divide and conquer** strategy is brilliant for managing
complexity.
It breaks down a large, overwhelming problem into smaller, more
manageable **compartments** or modules.
Each compartment is designed to be a focused, self-contained solution
for its specific part of the bigger problem, by design
**hiding its internal details** and exposing only what's necessary.
This promotes clarity and independent development.
The downside is that while beneficial for the individual part, this
inherent "black-boxing" can make it harder to get a holistic view of
the system's overall behavior or to optimize across these boundaries.
If taken too far without a strong overarching design or integration
strategy, these isolated compartments can act like technical
**silos**, where understanding interactions or making system-wide
changes becomes difficult because details are deliberately obscured
and the scope of each part is strictly limited.

### Holistic ⇌ Doing Everything, Everywhere, All at Once

Thinking **holistically** means looking at the whole system and how
everything connects.
This can prevent narrow thinking and lead to stronger designs.
But take it too far, and "holistic" can turn into trying to do
**everything, everywhere, all at once**.
That usually means getting stuck in analysis paralysis, drowning in
complexity, and never actually finishing anything.

### Integrated ⇌ Entangled

**Integration** lets different parts of a system work together
smoothly.
When done right, it makes for a unified and efficient product.
But without clear boundaries and interfaces, integration easily
leads to **entanglement**.
Components get so tightly woven together that changing, testing, or
replacing one piece becomes a nightmare for everything else.

### Best Practice ⇌ We've Always Done It Like That

A **best practice** should be a proven, effective method that really
works.
It should imply continuous improvement.
Too often though, "best practice" just means
"**we've always done it like that**."
This mindset stops critical thinking, blocks new ideas, and prevents
adapting to new tech or needs, turning a good concept into a rigid
excuse.

### Explicit ⇌ Verbose

Being **explicit** in code and design is generally great for
clarity, making intentions clear and reducing guesswork.
This helps future you (or someone else) understand things faster.
The problem is when being explicit goes overboard and becomes
excessive **verbosity**.
Overly wordy code can be harder to read and maintain, ironically
burying the clarity you were trying to achieve.

### Predictable ⇌ Boring

In terms of how a system behaves, **predictability** is super
important.
A predictable system is reliable, stable, and easier to troubleshoot.
No nasty surprises.
On the flip side, from a creative standpoint, predictable results
can be a bit **boring**.
They don't offer new insights or big breakthroughs.
But for critical systems, "boring" often means "it just works,"
which is exactly what you want.

### Mechanical Sympathy ⇌ Leaky Abstraction

**Mechanical sympathy** means really understanding the underlying
hardware or system to write super efficient code.
It's about knowing how things *actually* work at a lower level to
squeeze out performance.
This often means you're intentionally dealing with a
**leaky abstraction**.
To get those performance gains, you have to peer through or even
break the higher-level abstractions that were supposed to hide
those details.

---

## Conclusion

The pursuit of "good" software engineering often involves navigating
these inherent tensions. Rarely is a principle purely beneficial
without some form of trade-off or potential downside. Recognizing
both sides of the coin allows for more nuanced decision-making,
helping us to leverage the strengths of a concept while mitigating
its weaknesses. It's about informed compromise and conscious
decision-making, not blind adherence.