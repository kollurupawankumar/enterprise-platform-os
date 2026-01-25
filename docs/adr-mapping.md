# ADRs Implemented Mapping

This document maps architectural decision records (ADRs) to the actual implementations present in main.

- ADR 0001: Frontend Architecture
  - Implemented: React + Vite SPA skeleton, React Router routing, Monaco editor placeholder, DI data layer with mock/api data modes.

- ADR 0002: Service Injection & Data Layer Abstraction
  - Implemented: DataService interfaces, MockDataService, ApiDataService, serviceFactory, DataContext, and a mode switch via VITE_DATA_MODE.

- ADR 0003: RBAC
  - Implemented: ADR-based policy documented; UI guards scaffolded with comments. (Note: Full RBAC enforcement to be wired with useRBAC hook and backend checks in a follow-up).

- ADR 0004: JWT Storage & Session Management
  - Implemented: Guidance documented; in-code in-memory storage approach noted. (Not fully wired in this skeleton yet; planned for future integration).

- ADR 0005: Metadata Versioning & Activation
  - Implemented: Versioning concepts documented; UI skeleton supports activation/diff in a future step.

- ADR 0006: API Contract & Endpoints
  - Implemented: API contract reflected in ApiDataService endpoints; mock mirrors surface for testing.

- ADR 0007: Mock Data vs API Data Mode wiring
  - Implemented: Environment-driven mode switching in DI and service factory.

Notes
- This mapping helps maintain traceability between decisions and concrete implementation.
