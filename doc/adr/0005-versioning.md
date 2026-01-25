# ADR 0005: Metadata Versioning & Activation

Status: Accepted

Context
- PRD defines metadata versioning with states: Draft, Active, Archived, and an activation flow that updates the runtime config pointer for a subject area/entity.
- Need an audit trail for activations and diffs between versions.

Decision
- Each metadata item supports versions with lifecycle: Draft (editable), Active (runtime), Archived (historical).
- Activation updates the runtime reference and logs details: who activated, when, and from which version to which.
- UI should expose a diff view to compare versions before activation.

Rationale
- Enables auditable promotion of changes into production and rollback via archived versions.

Consequences
- Backend APIs must support versioning endpoints and audit fields.
- UI should present a version history and a diff view.
