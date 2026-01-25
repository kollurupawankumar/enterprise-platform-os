# ADR 0002: Service Injection & Data Layer Abstraction

Status: Accepted

Context
- The UI must work with both mock JSON fixtures and real API calls, per the DI requirement in the PRD.
- The data layer should expose stable interfaces for Subject Areas, Entities, Metadata, Runs, and DLQ.

Decision
- Implement a lightweight Dependency Injection (DI) pattern using a ServiceLocator that provides the current data service implementation.
- Define TypeScript interfaces for core services (e.g., SubjectAreasService, EntitiesService, MetadataService, RunsService).
- Provide two concrete implementations:
  1. MockDataService (reads from JSON fixtures or in-memory fixtures).
  2. ApiDataService (uses Axios to call backend endpoints).
- Switching between implementations is controlled by environment variable VITE_DATA_MODE=mock|api at runtime.
- Keep API surface stable; all methods should return promises to align with async API calls in UI.

Rationale
- Centralizes data access and makes it trivial to swap data sources without changing business logic.
- Encourages clean separation between components and data fetching logic.

Consequences
- Requires keeping mock fixtures in sync with API responses for deterministic behavior.
- Type coverage must be comprehensive to avoid runtime mismatches between mock and API implementations.
- Tests should cover both data modes.
