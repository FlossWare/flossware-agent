# ADR 0004: Interaction retention and redaction semantics

## Status

Accepted

## Context

`InteractionRepository` intentionally omits `delete(...)` while other repository ports
include it. Issue #7 asked whether that was an oversight or a retention decision.

Interactions are the durable conversation record for an agent session. Treating them as
ordinary deletable CRUD rows risks silent audit loss and forces every platform into the
same physical-delete model. Privacy and compliance needs (e.g. redaction of message
content) are real, but they are not the same as generic delete-for-symmetry.

## Decision

1. **Append-only by default.** Interaction history is not deleted through a general
   repository `delete()` method. `InteractionRepository` remains without `delete(...)`.

2. **No generic `InteractionRepository.delete()`.** Port symmetry alone is not a reason
   to add deletion to the domain contract.

3. **Redaction is a future domain transition.** When implemented, redaction will be an
   explicit operation on `Interaction` (e.g. `redact(String reason)`), callable only in
   defined states, returning a new instance with message *content* scrubbed while
   preserving identity, session linkage, lifecycle state, timestamps, structural message
   count/roles, and an auditable redaction record (reason + timestamp). Persistence uses
   the existing `save(...)` path. Corresponding domain events may be introduced with that
   implementation.

4. **Physical purge is out of the core library for now.** Irreversible erasure is a
   platform/infrastructure concern. Adapters and retention policy engines in
   `flossware-agent-platform` own physical deletion when a concrete compliance requirement
   exists. A portable `purge(...)` port will not be added to `flossware-agent` until a
   new ADR demonstrates a shared, implementable contract across adapters.

5. **Redaction does not imply automatic memory deletion.** Derived `Memory` records are
   not automatically deleted when an interaction is redacted. Platforms must be able to
   evaluate each memory under policy (redact, invalidate, or retain). That requires
   optional `InteractionId` provenance on `Memory`, tracked as a separate,
   version-sensitive change (not part of this ADR's code scope).

6. **Retention policy stays platform-owned.** Schedules, legal basis, authorization, and
   cascading behavior remain outside the domain library (consistent with ADR 0001).

## Consequences

- Positive: clear audit posture; no weak optional purge contract; redaction can later
  share lifecycle style with other domain transitions.
- Negative: platforms that need physical erasure implement it locally until a future ADR;
  Memory provenance must be added before redaction-aware memory hygiene is complete.

## Follow-up work

1. Implement `Interaction.redact(...)` + tests + event vocabulary (separate issue).
2. Add optional `InteractionId` on `Memory` (separate issue / version discussion).
3. Platform retention/purge when a concrete requirement lands (platform ADR).

## Revisit when

A concrete compliance requirement demands a portable physical-erasure contract across
adapters, or multi-actor redaction authorization must be expressed in the domain model.
