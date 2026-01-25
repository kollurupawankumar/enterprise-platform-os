# ADR 0006: API Contract & Endpoints

Status: Accepted

Context
- PRD lists a set of REST endpoints for Auth, Subject Areas, Entities, Metadata, Runs, and DLQ.
- A stable contract is required so both MockService and ApiService can implement the same surface.

Decision
- Define a stable API contract with base path /api and endpoints matching the PRD:
  - Auth: /auth/login, /auth/logout, /auth/me
  - Subject Areas: /subject-areas, /subject-areas/{id}
  - Entities: /subject-areas/{id}/entities, CRUD for entities
  - Metadata: /entities/{id}/metadata/{type}/versions, etc.
  - Activation: /entities/{id}/activate/{version}
  - Runs: /runs/trigger, /runs, /runs/{runId}, /runs/{runId}/retry-stage/{stage}, /dlq/messages/{id}
- MockSurface mirrors these endpoints; ApiService uses Axios with a base URL (VITE_API_BASE).
- Ensure error shapes are consistent for UI error handling.

Rationale
- Consistent surface between mock and API facilitates testing and development.

Consequences
- Keeping mock data in sync with API responses requires discipline.
- CI should test both modes if possible.
