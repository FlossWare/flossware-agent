# ADR 0001: Keep the agent library infrastructure-neutral

## Status

Accepted

## Context

The FlossWare agent system needs reusable domain contracts while allowing the concrete platform to choose databases, queues, model providers, transports, and deployment technologies.

Previous projects mixed domain concepts with infrastructure implementations, making reuse and replacement difficult.

## Decision

`flossware-agent` will contain domain concepts, interfaces/ports, value objects, lifecycle semantics, and contract tests. Infrastructure implementations remain outside the library.

The platform may use PostgreSQL, Redis, OrientDB, filesystem storage, or other technologies, but none is a requirement of the library API.

Likewise, the library will not depend on Claude, Crush, OpenAI, MCP, or another specific provider/client.

## Consequences

### Positive

- Infrastructure can be replaced without redesigning the domain API.
- Multiple applications can consume the same contracts.
- Implementations can be polyglot where appropriate.
- Contract tests can validate adapters consistently.

### Negative

- Some behavior must be expressed through abstractions rather than convenient infrastructure-specific APIs.
- The platform must implement additional adapter code.

## Revisit when

A concrete infrastructure dependency becomes unavoidable for a domain guarantee. Such a dependency requires a new ADR rather than silently entering the core library.
