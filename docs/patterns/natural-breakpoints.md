---
tags:
  - Hexagonal Architecture
---

!!! warning "Draft"

    This page is a work in progress.

<!-- TODO: find a good opening image -->

Long-running processes are fragile. The longer a process runs
without persisting intermediate progress, the more work is lost
when something goes wrong — and the harder it is to recover,
observe, or hand off parts to a different implementation.

The idea is to identify **natural breakpoints** in a process:
points where you can persist the current state, creating a
durable checkpoint. Everything before the checkpoint is done.
Everything after it can happen later — on a different schedule,
in a different component, even by a different team. If the
process crashes, you resume from the last checkpoint, not from
the beginning.

<!-- TODO: develop the "what makes a breakpoint natural" angle.
     Not every step boundary is a good checkpoint. A natural
     breakpoint is one where:
     - the work so far is meaningful on its own
     - a human could inspect and understand the intermediate state
     - the continuation can be implemented independently
     - the process can be paused without leaving things in a
       broken/inconsistent state
-->

## The General Shape

The pattern always follows the same structure:

1. **Domain logic** decides something needs to happen and
   produces a durable record of that intent — the checkpoint.
2. **Persistence** stores it, typically atomically alongside
   whatever triggered it.
3. **A processor** reads pending checkpoints and fulfills them
   via the appropriate mechanism.
4. **Scheduling** triggers the processor at the right cadence.

This separates the *decision* ("this needs to happen") from the
*execution* ("make it happen"). The persistence in between makes
the decision durable — it survives crashes, restarts, and
arbitrary delays.

<!-- TODO: the checkpoint framing emphasizes recovery — if
     anything goes wrong after this point, you know exactly where
     to resume. This implies:
     - idempotency on the continuation side (you might replay)
     - the checkpoint captures enough state to resume without
       re-deriving anything
-->

## Examples

<!-- TODO: flesh these out with more detail, code where helpful.
     The outbox is ONE instance, not the headline. -->

**Saga / Process Manager.** A multi-step business process where
each step completion is persisted. If the process crashes, it
resumes from the last completed step. Each step is a checkpoint.

**Scheduled tasks.** "Send a reminder in 3 days." Persist the
intent with a due timestamp, a processor picks it up when the
time comes. Same shape as any checkpoint relay.

**Retry queues.** An operation failed. Persist "retry this" with
a backoff timestamp. A processor picks it up later. Dead letter
queues are the same idea with human intervention as the
continuation.

**Approval workflows.** "This needs manager approval." Persist
the pending approval, the process halts. When approval arrives,
the continuation picks up.

**Batch processing with cursors.** Process N records, persist
the offset. Next run continues where you left off. The offset
is the checkpoint.

**Transactional outbox.** Persist the intent to publish an event
atomically with the domain state change. A relay reads pending
messages and publishes them to the message broker. The outbox
message is the checkpoint between "something happened in the
domain" and "the outside world is notified." See also
[Adapter Isolation](adapter-isolation.md) for the architectural
context.

<!-- TODO: outbox details. Key tensions:
     - Outbox write must be atomic with aggregate save (same
       transaction, same adapter). The repository port can accept
       a messages parameter alongside the aggregate.
     - Outbox read + event publishing are separate concerns with
       separate adapters, coordinated by an application-level
       relay service.
     - Scheduling the relay is infrastructure (cron, @Scheduled,
       K8s CronJob).
     - When scaling to many aggregates, the outbox write logic
       shouldn't be reimplemented per repository. Consider how
       to share it cleanly.
-->

## Hexagonal Architecture Mapping

<!-- TODO: develop this section. Key insight from earlier
     discussion:

     In a hexagonal architecture, checkpoints are the natural
     integration points between adapters. The checkpoint is where
     one adapter's job ends and another's begins, with the domain
     in between deciding what goes in and the application layer
     scheduling what comes out.

     - Persisting the checkpoint = adapter concern (write side)
     - Reading pending checkpoints = adapter concern (read side,
       possibly same adapter, possibly different port)
     - Fulfilling the checkpoint = different adapter (different
       technology)
     - Orchestrating read + fulfill = application logic
     - Scheduling = infrastructure
-->

## References

* [Transactional Outbox Pattern](https://microservices.io/patterns/data/transactional-outbox.html)

<!-- TODO: find references for:
     - Saga pattern
     - Durable execution (Temporal)
     - Store-and-forward
     - Process Manager (Enterprise Integration Patterns?)
-->
