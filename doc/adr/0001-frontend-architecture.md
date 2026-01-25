# ADR 0001: Frontend Architecture

Status: Accepted

Context
- The PRD for the Metadata Platform UI (React + Vite) specifies a SPA UI with Monaco editors, Axios-based API calls, and support for both MOCK and API data modes.
- Tech goals: fast dev experience, type-safety (TypeScript), modular UI with clean separation between UI and data access, and a simple way to switch data sources without code changes.

Decision
- Use a React single-page application built with Vite and TypeScript.
- UI routing via React Router for modular screens (Dashboard, Subject Areas, Entities, Metadata Editors, Runs, Run Details).
- Monaco Editor for YAML/JSON metadata editing; optional fallbacks if Monaco is unavailable.
- Axios for API calls; a small data layer abstracts network vs fixture data access.
- Data mode switching via environment variable VITE_DATA_MODE=mock|api to select MockService or ApiService at runtime.
- Data access services injected via a simple DI pattern.

Rationale
- Keeps the architecture simple, testable, and extensible for future features (e.g., DI, logging, analytics).

Consequences
- Build-time and runtime must ensure the chosen mode (mock vs api) is honored; faluses gracefully if endpoints are unreachable in api mode.
- Consumers must understand the environment variable to switch modes; CI should test both paths.
- Mock data should mirror API responses.
