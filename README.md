# FlossWare Agent

A reusable, infrastructure-neutral agent library for building persistent AI-agent systems.

## Purpose

`flossware-agent` defines the domain model and contracts used by FlossWare agent applications and platforms. It deliberately does **not** define a database, queue, HTTP server, model provider, MCP implementation, or deployment environment.

The concrete platform is [`flossware-agent-platform`](https://github.com/FlossWare/flossware-agent-platform).

## Architectural position

```text
                    flossware-agent
                 reusable domain/contracts
                           │
                           ▼
              flossware-agent-platform
                 concrete platform
                           │
              ┌────────────┼────────────┐
              ▼            ▼            ▼
            REST          MCP         workers
```

## Core concepts

The initial domain model will cover:

- Agent
- Session
- Interaction
- Message
- Context
- Memory
- Task
- Tool
- Model
- Provider
- Event
- Artifact

See [ARCHITECTURE.md](ARCHITECTURE.md) and the ADRs for decisions as the model evolves.

## Design principles

1. **Contracts before implementations.**
2. **Infrastructure neutrality.** PostgreSQL, Redis, OrientDB, filesystems, and other technologies are adapters/implementations.
3. **Provider neutrality.** The library does not depend on Claude, OpenAI, Crush, MCP, or a particular SDK.
4. **Persistence is explicit.** Sessions, interactions, and durable memories must be representable independently of a client process.
5. **Polyglot where useful.** Language choices are implementation decisions, not domain constraints.
6. **Stable identifiers and lifecycle semantics.** State must remain recoverable across process and client restarts.
7. **Testable contracts.** Implementations should be able to run shared contract tests.

## Status

Early architecture phase. See GitHub issues for the implementation sequence.
