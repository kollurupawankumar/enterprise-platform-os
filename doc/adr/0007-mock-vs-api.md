# ADR 0007: Mock Data vs API Data Mode wiring

Status: Accepted

Context
- UI must support two modes: MOCK (fixtures) and API (real backend).
- A simple mechanism is needed to switch modes without code changes.

Decision
- Implement a data layer switch controlled by VITE_DATA_MODE (mock|api).
- Provide a single entry point to obtain the current data service implementation (MockDataService or ApiDataService).
- Ensure both implementations expose the same async API surface for consistency.
- Provide a health check or fallback if API is unreachable.

Rationale
- Enables offline/demo capability and easier local development.

Consequences
- Mock fixtures must be kept in sync with API changes.
- Tests should cover both modes to avoid regressions.
