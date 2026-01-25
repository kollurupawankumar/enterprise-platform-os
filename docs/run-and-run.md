# Build and Run MDHCP-UI (Dev & Prod)

This guide explains how to build and run the Metadata Platform UI in development and production environments, with environment-driven DI switching between mock data and real API data.

Prerequisites
- Node.js (14+ or 16+ recommended)
- npm or pnpm
- Git

1) Development (dev server)
- Install dependencies: `npm ci` (from the repo root, ensure you’re in the mdhcp-ui folder)
- Start in Mock mode (default):
  - macOS/Linux: `VITE_DATA_MODE=mock npm run dev`
  - Windows (PowerShell): `$env:VITE_DATA_MODE = 'mock'; npm run dev`
- Start in API mode: 
  - macOS/Linux: `VITE_DATA_MODE=api VITE_API_BASE=https://api.example.com npm run dev`
  - Windows: `$env:VITE_DATA_MODE = 'api'; $env:VITE_API_BASE = 'https://api.example.com'; npm run dev`
- Access: http://localhost:5173
- DI behavior: The UI will load data through the injected data service (MockDataService or ApiDataService) based on VITE_DATA_MODE. The API base URL is configurable via VITE_API_BASE.

2) Production (build & serve)
- Build for production: `VITE_DATA_MODE=api VITE_API_BASE=https://api.example.com npm run build`
- Serve static dist: use a static file server (e.g., `npx serve -s dist`)
- Optional Docker (basic example):
  - Dockerfile (mdhcp-ui/Dockerfile)
  - Build: `docker build -t mdhcp-ui:prod .`
  - Run: `docker run -p 8080:80 mdhcp-ui:prod`
- In production, ensure the API base URL is reachable and CORS configured if needed.

3) Docker example (single-file Dockerfile)
```
# mdhcp-ui/Dockerfile
FROM node:18 as builder
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/mdhcp-ui/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

Notes
- The mock mode is useful for local development and demos; switch to API mode when connecting to real backend.
- If you’re deploying behind a reverse proxy, pass through the environment var VITE_API_BASE to configure backend URL.
- If you want to reuse Docker in CI/CD, you can implement a multi-stage build in your pipeline to produce a production image.
