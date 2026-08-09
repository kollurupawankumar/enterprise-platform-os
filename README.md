# 🏢 Society OS - Enterprise Housing Society Operating System

A modular enterprise desktop platform built with **Java 17**, **Spring Boot 3.5**, **JavaFX 21**, **SQLite**, and **Offline AI Engine**.

---

## 🏗️ Architecture & Core Modules

The system is structured as a clean multi-module architecture:

### 🏛️ Core OS Platform (`core-os/`)
- **`core-commons`**: Shared entities, currency formatters (`INR`), DateTime utilities.
- **`core-security`**: User authentication, BCrypt hashing, RBAC.
- **`core-storage`**: File upload and disk storage engine.
- **`core-audit`**: System audit logger (`AuditTrailEntity`).
- **`core-notification`**: Multi-channel notification dispatcher.
- **`core-ai`**: Cross-platform offline AI engine (`llama-server` launcher + `Qwen2.5 GGUF`).
- **`core-ui-shell`**: Base UI shell contracts, navigation managers, side drawer.
- **`core-config`**: SQLite database ZIP backup and restore engine.
- **`core-reporting`**: Universal CSV/Excel report exporter.
- **`core-search`**: Global multi-provider search engine.

### 🏡 Domain OS Application (`domain-os/society-os/`)
- **`Member Management`**: Registration, KYC, joint owners, nominees.
- **`Property & Occupancy`**: Flat allocations, ownership history, tenant records.
- **`Finance & Billing`**: Maintenance bill generation, expense vouchers, bank accounts.
- **`Governance`**: General body meetings, resolutions, online meeting links.
- **`Operations`**: Equipment AMCs, vendor registers, staff shift rosters.
- **`AI Copilot`**: Real-time SQLite database RAG & Bye-Laws AI drawer.

---

## 🚀 Getting Started

### 1. Build the Entire System
```bash
mvn clean install -DskipTests
```

### 2. Run Desktop Application
```bash
mvn javafx:run -pl domain-os/society-os
```

### 3. Packaging & Distribution
Executing `mvn clean install -DskipTests` automatically assembles the production distribution folder under `dist/`.

- **macOS / Linux**: `./dist/launch-mac.sh`
- **Windows**: `dist\launch-windows.bat`
- **Standalone Portable Zip**: `dist/installers/`
