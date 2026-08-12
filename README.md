# FlossWare Agent

A reusable, infrastructure-neutral agent library for building persistent AI-agent systems.

## Purpose

`flossware-agent` defines the domain model and contracts used by FlossWare agent
applications and platforms. It deliberately does **not** define a database, queue,
HTTP server, model provider, MCP implementation, or deployment environment.

The concrete platform is [`flossware-agent-platform`](https://github.com/FlossWare/flossware-agent-platform).

## Architectural position

```text
                    flossware-agent
                 reusable domain/contracts
                           |
                           v
              flossware-agent-platform
                 concrete platform
                           |
              +------------+------------+
              v            v            v
            REST          MCP         workers
```

## Core concepts

| Concept | Abstraction |
| --- | --- |
| Agent | Entity (definition + status) |
| Session | Entity (durable, resumable) |
| Interaction | Entity (unit of exchange) |
| Message | Immutable communication value |
| Context | Assembled value object |
| Memory | Durable recall entity |
| Task | Durable work entity |
| Tool | Definition + invocation values |
| Model / Provider | Descriptor + `ModelProvider` port |
| Event | Domain event value |
| Artifact | Durable output entity |

See [ARCHITECTURE.md](ARCHITECTURE.md) and [docs/adr/](docs/adr/).

## Design principles

1. Contracts before implementations.
2. Infrastructure neutrality.
3. Provider neutrality.
4. Persistence is explicit; sessions are resumable.
5. Stable identifiers and lifecycle semantics.
6. Immutability: transitions return new instances with the same identity.
7. Testable contracts.

## Versioning

FlossWare projects use **X.Y** versions only (no `X.Y.Z`, no `-SNAPSHOT`), as enforced by
[`build-tools`](https://github.com/FlossWare/build-tools).

This library is **1.0**. Published coordinates:

```xml
<dependency>
  <groupId>org.flossware</groupId>
  <artifactId>flossware-agent</artifactId>
  <version>1.0</version>
</dependency>
```

Resolve from PackageCloud: `https://packagecloud.io/flossware/java/maven2/`

## Build

Requires JDK 21+.

```bash
mvn clean verify
```

## Package layout

```text
org.flossware.agent
├── domain  (id, agent, session, interaction, message, context, memory, task, tool, model, event, artifact)
└── port    (repositories, ModelProvider, ToolExecutor, EventPublisher)
```

## Status

Domain model and public contracts for issue #1 are implemented. Version **1.0** is the
first release aligned with FlossWare build-tools conventions.
