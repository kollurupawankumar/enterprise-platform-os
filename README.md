# 💻 Enterprise Platform OS (`enterprise-platform-os`)

An enterprise-grade modular application platform built with **Java 17**, **Spring Boot 3.5**, **JavaFX 21**, **SQLite**, and an **Offline AI Engine**.

The architecture decouples reusable core platform capabilities (`core-os/`) from industry-specific business modules (`domain-os/`), enabling quick deployment of domain applications (Housing Societies, Commercial Facilities, Healthcare, School Management, etc.).

---

## 🏗️ Architecture & Core Modules

The repository follows a clean, extensible two-tier architecture:

```text
enterprise-platform-os/
 ├── core-os/                      <-- Reusable Platform Kernel
 │    ├── core-commons              <-- Base entities, currency formatters, date utilities
 │    ├── core-security             <-- User context, BCrypt security, RBAC
 │    ├── core-storage              <-- Local disk & file management service
 │    ├── core-audit                <-- System-wide audit trail explorer logger
 │    ├── core-notification         <-- Multi-channel dispatcher (Email, Teams, Slack)
 │    ├── core-ai                   <-- Cross-platform offline AI engine (llama-server + Qwen2.5)
 │    ├── core-ui-shell             <-- Navigation contracts, base layouts, drawer host
 │    ├── core-config               <-- Database backup & restore engine
 │    ├── core-reporting            <-- Universal CSV/Excel report exporter
 │    └── core-search               <-- Multi-provider search engine
 │
 └── domain-os/                    <-- Domain Application Layers
      └── society-os                <-- Housing Society Operating Application
           ├── Member & Property Registers
           ├── Governance & Bye-Laws RAG AI
           └── Financial Vouchers & AMC Tracking
```

---

## 🚀 Getting Started

### 1. Build the Multi-Module Platform
```bash
mvn clean install -DskipTests
```

### 2. Launch the `Society OS` Domain Application
```bash
mvn javafx:run -pl domain-os/society-os
```

### 3. Native Distribution & Desktop Packaging
Running `mvn clean install` automatically assembles production-ready launchers in the `dist/` directory:

- **macOS / Linux Launcher**: `./dist/launch-mac.sh`
- **Windows Launcher**: `dist\launch-windows.bat`
