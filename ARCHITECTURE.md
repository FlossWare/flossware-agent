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

## What does not belong here

- PostgreSQL, Redis, OrientDB, pgvector schemas
- REST, HTTP frameworks, MCP servers
- Provider SDKs (Anthropic, OpenAI, etc.)
- Agent runtimes, orchestration engines, workers
- Auth, deployment, observability infrastructure

## Lifecycle ownership

```text
Agent
  └── Session          (resumable by SessionId)
        └── Interaction
              └── Message  (append while STARTED)
```

Tasks, memories, and artifacts are owned by an agent and may reference a session.

## Persistence ports

`org.flossware.agent.port`: AgentRepository, SessionRepository, InteractionRepository,
MemoryRepository, TaskRepository, ArtifactRepository, ModelProvider, ToolExecutor,
EventPublisher.

## Extension points for the platform

Implement ports, assemble Context, drive state machines, publish DomainEvents.
