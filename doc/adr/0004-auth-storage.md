# ADR 0004: JWT Storage & Session Management

Status: Accepted

Context
- PRD requires JWT-based authentication with secure handling of tokens and RBAC.
- Best practices suggest avoiding storing access tokens in localStorage; prefer in-memory storage and HttpOnly cookies for refresh tokens.

Decision
- Store access tokens in an in-memory React context (not persisted across refreshes).
- Do not persist tokens in localStorage/sessionStorage.
- Rely on HttpOnly refresh tokens via secure cookies for token refresh.
- On logout, clear in-memory tokens and redirect to login.
- Implement token lifecycle within ApiService/Auth flow; ready for server-supported refresh.

Rationale
- Reduces risk of token theft via XSS; cookies reduce token leakage risk.

Consequences
- Page reloads require re-auth; refresh via cookie should reissue access token.
- Server must issue HttpOnly cookies for refresh tokens.
- Client must manage token expiry and trigger refresh via API calls.
