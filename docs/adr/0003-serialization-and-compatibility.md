# ADR 0003: Serialization and compatibility expectations

## Status

Accepted

## Context

Issue #1 requires compatibility and serialization expectations where appropriate.
This library must not depend on Jackson, protobuf, or any serialization framework,
but platform adapters need a clear contract for what is stable to persist, transmit,
and restore.

## Decision

### Guaranteed by this library

1. **Identifiers** — each `*Id` exposes a non-blank `String value()`. Equality is
   by that string. Adapters MUST persist and restore the string exactly.
2. **Enums** — lifecycle and classification enums use their Java enum constant
   names (`SessionState.ACTIVE.name()` → `"ACTIVE"`). Adapters MUST serialize by
   name, not ordinal.
3. **Timestamps** — `Instant` fields use the ISO-8601 representation produced by
   `Instant.toString()` / `Instant.parse(...)`.
4. **Identity** — entity equality is by id only. Restoring an entity with the same
   id and field values MUST be equal to the original under `equals`.
5. **Restore factories** — entities that support persistence provide static
   `restore(...)` (or equivalent) factories that accept the full field set.
   Adapters reconstruct domain objects via these factories, not via reflection
   into private constructors.
6. **Immutability of collections** — maps and lists exposed by domain types are
   unmodifiable. Adapters may copy them for storage; they MUST NOT rely on
   mutating returned collections.

### Intentionally left to adapters

1. Wire format (JSON, MessagePack, SQL columns, document documents, etc.).
2. Null vs absent optional fields in external representations.
3. Schema versioning and migration of stored documents.
4. Encoding of nested opaque strings (tool parameter schemas, artifact locators,
   model parameter maps) beyond "opaque UTF-8 text".
5. Transport framing (HTTP body, message queue payload, MCP content).

### DomainEvent payload

`DomainEvent.payload()` is `Map<String, String>` by deliberate limitation for this
contract generation. Producers encode structured data as strings if needed.
This keeps the library free of JSON trees and serialization frameworks. If the
event bus becomes a primary integration boundary requiring nested structure,
revisit with a provider-neutral structured type (still without framework coupling).

## Consequences

Platform persistence and messaging layers have a minimal, language-friendly
contract: strings for ids, enum names, ISO-8601 instants, and restore factories.
No serialization library is required or implied by `flossware-agent`.

## Revisit when

- Polyglot clients need a formal IDL or schema registry.
- Nested structured event payloads become a core requirement.
- Message content needs multimodal parts beyond a single string.
