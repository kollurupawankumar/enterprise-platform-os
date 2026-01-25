# ADR 0003: Role-Based Access Control (RBAC)

Status: Accepted

Context
- PRD defines four roles: Admin, Data Engineer, Support Engineer, Viewer.
- UI and API should enforce permissions according to these roles to prevent unauthorized actions.

Decision
- Implement a formal RBAC model in the frontend and extend API checks where feasible.
- Roles and permissions mapped as:
  - Admin: MANAGE_SUBJECTS, EDIT_METADATA, ACTIVATE_METADATA, TRIGGER_RUN, RETRY_STAGE, VIEW_DLQ, VIEW_SPARK
  - Engineer: MANAGE_SUBJECTS, EDIT_METADATA, TRIGGER_RUN, RETRY_STAGE, VIEW_DLQ, VIEW_SPARK
  - Support: VIEW_DLQ, RETRY_STAGE, VIEW_SPARK
  - Viewer: VIEW_SPARK
- Implement a useRBAC hook and a WithPermission wrapper to guard UI elements.
- Backend endpoints should enforce RBAC, returning 403 on forbidden actions.

Rationale
- Centralizes policy for auditability and reduces scattered checks across components.
- Frontend guards provide quick feedback; backend guards ensure security if UI is bypassed.

Consequences
- UI will render or enable/disable controls based on role/permissions.
- Tests should cover permissions for all roles.
