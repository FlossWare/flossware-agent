# Architecture

## Boundary

`flossware-agent` is a reusable library of domain concepts and stable contracts.
It is not the runtime platform.

```text
application/platform
        |
        v
flossware-agent contracts
        ^
        |
  implementations
```

## What belongs here

- Domain entities and value objects
- Explicit lifecycle state machines
- Strongly typed identifiers with value equality
- Provider-neutral model request/response types
- Repository and provider ports
- Contract tests
- Serialization *expectations* (not frameworks)

## What does not belong here

- PostgreSQL, Redis, OrientDB, pgvector schemas
- REST, HTTP frameworks, MCP servers
- Provider SDKs (Anthropic, OpenAI, etc.)
- Agent runtimes, orchestration engines, workers
- Auth, deployment, observability infrastructure
- Serialization libraries (Jackson, protobuf, etc.)

## Lifecycle ownership

```text
Agent
  └── Session          (resumable by SessionId)
        └── Interaction
              └── Message  (append while STARTED)
```

Tasks, memories, and artifacts are owned by an agent and may reference a session.

## Serialization and compatibility

This library does not ship a serializer. Adapters are expected to honor:

| Kind | Canonical representation |
| --- | --- |
| Identifiers | non-blank `String` from `id.value()` |
| Enums | enum constant name (`ACTIVE`, not ordinal) |
| Instants | ISO-8601 via `Instant.toString()` / `Instant.parse` |
| Entities | reconstruct via public `restore(...)` / factory methods |
| Collections | treat returned lists/maps as unmodifiable snapshots |

Wire format, schema versioning, and transport framing are platform concerns.
See [docs/adr/0003-serialization-and-compatibility.md](docs/adr/0003-serialization-and-compatibility.md).

`DomainEvent` payload is deliberately `Map<String, String>` in this generation.

## Persistence ports

`org.flossware.agent.port`: AgentRepository, SessionRepository, InteractionRepository,
MemoryRepository, TaskRepository, ArtifactRepository, ModelProvider, ToolExecutor,
EventPublisher.

## Extension points for the platform

Implement ports, assemble Context, drive state machines, publish DomainEvents,
map domain fields to storage using the serialization expectations above.
