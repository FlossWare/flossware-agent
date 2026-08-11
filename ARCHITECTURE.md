# Architecture

## Boundary

`flossware-agent` is a reusable library. Its responsibility is to define agent-domain concepts and stable contracts. It is not the runtime platform.

The dependency direction is:

```text
application/platform
        │
        ▼
flossware-agent contracts
        ▲
        │
  implementations
```

Implementations may provide persistence, model providers, tools, event transport, and other infrastructure without changing the domain contracts.

## Domain model

### Agent

A configured autonomous or assisted actor capable of processing interactions and executing tasks.

### Session

A durable logical conversation/execution context. A session must have a stable identifier and must be resumable independently of the client process.

### Interaction

A unit of exchange or execution within a session. Interactions provide provenance for messages, tool calls, model responses, and derived state.

### Message

A typed piece of communication associated with an interaction. The model must support roles and metadata without coupling them to one provider's message format.

### Context

The information made available to an agent for a particular operation. Context may be assembled from messages, memories, tools, knowledge, and runtime state.

### Memory

Durable information intentionally retained for later recall. Memory is distinct from raw interaction history.

### Task

A durable unit of work that may outlive a single interaction or process.

### Tool

A capability an agent may invoke. Tool definitions and results must remain provider-neutral.

### Model / Provider

A model is the logical inference capability. A provider supplies access to one or more models. Provider implementations belong outside this library.

### Event

A durable or transient fact describing a domain change or lifecycle transition. Transport is implementation-specific.

### Artifact

A durable output or object associated with an agent operation, such as a generated file, report, or other externally stored result.

## State and identity

Domain objects require stable identifiers. Lifecycle state must be explicit and serializable where persistence is required. Implementations must not depend on in-memory object identity for correctness.

## Persistence ports

The library will define contracts for storing and retrieving sessions, interactions, memories, tasks, events, and artifacts. These ports must not expose database-specific concepts.

## Model/tool ports

Model and tool interactions will use provider-neutral request, response, streaming, cancellation, timeout, and error semantics.

## Explicit non-goals

This repository does not own:

- PostgreSQL schemas
- Redis queues
- OrientDB schemas
- REST endpoints
- MCP server implementation
- Authentication infrastructure
- Container/orchestration deployment
- A specific model provider
- A specific agent client such as Crush or Claude

Those belong in consuming applications, the platform, or adapters.
