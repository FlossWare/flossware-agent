# ADR 0002: Domain model abstraction choices

## Status

Accepted

## Context

Issue #1 requires a foundational domain model for Agent, Session, Interaction,
Message, Context, Memory, Task, Tool, Model, Provider, Event, and Artifact.

## Decision

| Concept | Abstraction |
| --- | --- |
| Identifiers | Immutable value records (value equality, string-serializable) |
| Agent, Session, Interaction, Memory, Task, Artifact | Immutable entities (transitions return new instances) |
| Message | Immutable (equality by id) |
| Context | Immutable value object (no identity) |
| ToolDefinition, ToolInvocation, ModelDescriptor, ModelRequest/Response | Immutable values |
| DomainEvent | Immutable domain event; payload is `Map<String, String>` (see ADR 0003) |
| Repository / ModelProvider / ToolExecutor / EventPublisher | Ports (interfaces) |
| SessionState, InteractionState, TaskState, AgentStatus, MessageRole, MemoryKind, EventType | Enums |

Lifecycle methods never mutate in place.

## Consequences

Platform can persist and resume sessions by id and state. Provider adapters map
domain request/response without leaking SDKs. Callers reassign after transitions.

## Revisit when

Multimodal message parts or new lifecycle states are required.
