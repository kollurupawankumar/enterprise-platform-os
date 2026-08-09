# TECHNICAL DESIGN & ARCHITECTURE SPECIFICATION
## Enterprise Platform OS — Society Office OS Domain Module
**Document Version**: 2.0  
**Author**: Antigravity Engineering Team  
**Target Platform**: Single-User / Local-Network Desktop Enterprise Application  
**Tech Stack**: Java 17+, Spring Boot 3.5.4, JavaFX 21, SQLite 3.49, Flyway 10, Hibernate ORM 6.6, Maven Reactor Architecture  

---

## 1. High-Level Architectural Overview

### 1.1 Architectural Style & Design Principles
The **Enterprise Platform OS** is designed as a **Modular Enterprise Reactor Monolith** using a **Decoupled 2-Tier Architecture**:
1. **Core OS Layer (`core-os/*`)**: 10 domain-agnostic, reusable platform engine modules providing cross-cutting infrastructure services (Security, Storage, Audit, Notification, AI, UI Shell, Backup Config, Universal Reporting, Universal Search, Commons Utilities).
2. **Domain OS Layer (`domain-os/*`)**: Industry-specific business domain applications built on top of the Core OS engines. **Society Office OS** (`domain-os/society-os`) is the primary desktop application for Housing Society Operations.

```
                    ┌────────────────────────────────────────────────────────┐
                    │                   JavaFX 21 UI Shell                   │
                    │               (Spring-Managed Controllers)              │
                    └───────────────────────────┬────────────────────────────┘
                                                │
                    ┌───────────────────────────▼────────────────────────────┐
                    │               Society OS Domain Application            │
                    │                  (domain-os/society-os)                │
                    └───────┬────────────────────────────────────────┬───────┘
                            │                                        │
┌───────────────────────────▼────────────────────────────┐  ┌──────────▼───────────────────────────┐
│                    Core OS Platform                    │  │          Data & Security            │
│                       (core-os/*)                      │  │                                         │
│  ├── core-commons        ├── core-notification         │  │  ├── SQLite Embedded DB (`society.db`) │
│  ├── core-security       ├── core-ai                   │  │  ├── Flyway Schema Migration (V1..V22) │
│  ├── core-storage        ├── core-ui-shell             │  │  ├── Spring Data JPA & Hibernate 6     │
│  ├── core-audit          ├── core-config               │  │  └── BCrypt Password Encoder           │
│  ├── core-reporting      └── core-search               │  │                                         │
└────────────────────────────────────────────────────────┘  └─────────────────────────────────────────┘
```

---

## 2. Maven Multi-Module Reactor Structure

The reactor root `pom.xml` orchestrates 11 build sub-modules:

```
enterprise-platform-os/
├── pom.xml                               # Parent Reactor POM (Dependency Management)
├── docs/                                 # Technical & Use-Case Architecture Specs
│   ├── USECASE_SPECIFICATION.md
│   └── TECHNICAL_DESIGN_SPECIFICATION.md
├── core-os/                              # Reusable Core Engine Modules
│   ├── core-commons/                     # Base DTOs, Exceptions, Spring Context Utilities
│   ├── core-security/                    # User Authentication & Role-Based Security
│   ├── core-storage/                     # File Storage Engine (Uploads, Local Receipts)
│   ├── core-audit/                       # Automated JPA Entity Auditing & Event Logging
│   ├── core-notification/                # Email & In-App Alert Dispatcher Engine
│   ├── core-ai/                          # Offline Llama C++ RAG Interface Engine
│   ├── core-ui-shell/                    # JavaFX UI Controls & Base Navigation Manager
│   ├── core-config/                      # Automated ZIP Database Backup Engine
│   ├── core-reporting/                   # Universal CSV/Excel Exporter Engine
│   └── core-search/                      # Multi-Provider Universal Search Engine
└── domain-os/                            # Domain Operations Applications
    └── society-os/                       # Housing Society Desktop OS Application
        ├── src/main/java/com/society/   # Spring Boot + JavaFX Controller Packages
        ├── src/main/resources/
        │   ├── fxml/                     # JavaFX Layout Templates
        │   ├── css/                      # Enterprise UI Theme Stylesheets
        │   ├── db/migration/             # Flyway SQL Migration Scripts (V1..V22)
        │   └── application.yml           # Spring Boot Profiles & Database Config
        └── pom.xml
```

---

## 3. Database Schema & Entity Design (SQLite + JPA)

### 3.1 Core Entity Relational Diagram Data Model

```
                    ┌────────────────────────┐
                    │        society         │
                    ├────────────────────────┤
                    │ id (PK)                │
                    │ name                   │
                    │ registration_number    │
                    │ member_number_format   │
                    │ membership_number_fmt  │
                    │ share_certificate_fmt  │
                    └───────────┬────────────┘
                                │ 1
                                │
                                │ N
                    ┌───────────▼────────────┐          1 ┌────────────────────────┐
                    │        members         ├───────────►│       properties       │
                    ├────────────────────────┤            ├────────────────────────┤
                    │ id (PK)                │            │ id (PK)                │
                    │ member_number          │            │ flat_number, wing      │
                    │ membership_number      │            │ area_sq_ft, floor      │
                    │ first_name, last_name  │            │ current_owner_id (FK)  │
                    │ mobile_number, email   │            └────────────────────────┘
                    │ status (ACTIVE/...)    │
                    └─────┬──────────────┬───┘
                          │ 1            │ 1
                          │              │
                        N │            N │
  ┌───────────────────────▼──┐  ┌────────▼──────────────────┐
  │    share_certificate     │  │        nominees          │
  ├──────────────────────────┤  ├──────────────────────────┤
  │ id (PK)                  │  │ id (PK)                  │
  │ certificate_number       │  │ member_id (FK)           │
  │ member_id (FK)           │  │ nominee_name             │
  │ from_share_number        │  │ relationship             │
  │ to_share_number          │  │ percentage_share         │
  │ total_shares, amount     │  └──────────────────────────┘
  └──────────────────────────┘
```

### 3.2 Database Table Specifications

#### 1. `society` (Society Profile & Dynamic Numbering Configurations)
| Column Name | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | INTEGER | PRIMARY KEY AUTOINCREMENT | Unique Society ID |
| `name` | TEXT | NOT NULL | Society Name |
| `registration_number` | TEXT | NOT NULL | Statutory Registration No |
| `member_number_format` | TEXT | DEFAULT `'MEM-{SEQ}'` | Dynamic Member No Format Pattern |
| `membership_number_format` | TEXT | DEFAULT `'SSTS/{YEAR}/{SEQ}'` | Dynamic Statutory Membership Format Pattern |
| `share_certificate_format` | TEXT | DEFAULT `'SC/{YEAR}/{SEQ}'` | Dynamic Share Cert Format Pattern |

#### 2. `members` (Statutory Member Directory - Form J Compliance)
| Column Name | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | INTEGER | PRIMARY KEY AUTOINCREMENT | Unique Member ID |
| `member_number` | TEXT | UNIQUE | Sequential Member No (`MEM-00007`) |
| `membership_number` | TEXT | UNIQUE | Formatted Membership No (`SSTS/2026/00007`) |
| `first_name` | TEXT | NOT NULL | First Name |
| `last_name` | TEXT | | Last Name |
| `gender` | TEXT | DEFAULT `'MALE'` | MALE / FEMALE |
| `dob` | TEXT | | Date of Birth (`YYYY-MM-DD`) |
| `mobile_number` | TEXT | NOT NULL | 10-Digit Mobile |
| `email` | TEXT | | Email Address |
| `status` | TEXT | DEFAULT `'ACTIVE'` | ACTIVE / INACTIVE / TRANSFERRED |

#### 3. `share_certificate` (Share Capital Distinctive Range Register - Form I Compliance)
| Column Name | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | INTEGER | PRIMARY KEY AUTOINCREMENT | Unique Certificate ID |
| `certificate_number` | TEXT | UNIQUE, NOT NULL | Certificate No (`SC/2026/00001`) |
| `member_id` | INTEGER | FOREIGN KEY (`members.id`) | Certificate Owner Member FK |
| `from_share_number` | INTEGER | NOT NULL | Distinctive Starting Share Number |
| `to_share_number` | INTEGER | NOT NULL | Distinctive Ending Share Number |
| `total_shares` | INTEGER | NOT NULL | Count of Shares (Default 10) |
| `total_amount` | REAL | NOT NULL | Total Valuation Amount (₹) |
| `issue_date` | TEXT | NOT NULL | Date of Share Allotment |
| `status` | TEXT | DEFAULT `'ACTIVE'` | ACTIVE / TRANSFERRED / CANCELLED |

---

## 4. Key Component Workflows & Algorithmic Designs

### 4.1 Resilient Header-Agnostic CSV Import Algorithm
To handle arbitrary Excel spreadsheets provided by society secretaries, `MemberController.java` implements a resilient parser:

```
User Selects CSV File
       │
       ▼
Read Header Row (Row 0)
       │
       ▼
Clean Header Tokens (lowercase, strip non-alphanumeric)
Build Column Map: Map<CleanHeader, ColumnIndex>
       │
       ▼
For Each Data Row (Row 1..N):
  ├── Extract First Name, Middle Name, Last Name via Header Map
  ├── Parse Flexible Date (Try: DD/MM/YY, DD/MM/YYYY, YYYY-MM-DD)
  ├── Validate / Auto-Generate Mobile Number (Fallback 10-Digit)
  ├── Check Member Number & Membership Number:
  │     If Missing -> Invoke NumberGeneratorService.nextMemberNumber() / nextMembershipNumber()
  └── Register Member DTO via MemberService.register()
       │
       ▼
Display Import Summary (Success Count vs Error List Dialog)
```

### 4.2 Distinctive Share Range Auto-Computation Algorithm (Option A)
To prevent manual data entry errors and share range overlap in `ShareServiceImpl.java`:

$$\text{From Share No} = \max(\text{to\_share\_number}) + 1 \quad (\text{Default } 1 \text{ if table empty})$$

$$\text{To Share No} = \text{From Share No} + \text{Total Shares} - 1$$

$$\text{Total Amount} = \text{Total Shares} \times \text{Face Value (₹ 50.00)}$$

---

## 5. UI Architecture & View Controllers

### 5.1 FXML Navigation Architecture (`NavigationManager.java`)
JavaFX Spring integration uses `SpringFXMLLoader` to instantiate controllers as Spring Beans, allowing full Dependency Injection of Spring `@Service` components into JavaFX `@FXML` controllers:

```java
@Component
public class NavigationManager {
    private final FxmlLoader fxmlLoader;
    private StackPane contentArea;

    public void navigate(View view) {
        Parent node = fxmlLoader.load(view.getFxmlPath());
        contentArea.getChildren().setAll(node);
    }
}
```

### 5.2 Registered Navigation Views (`View.java`)
- `DASHBOARD` $\rightarrow$ `/fxml/dashboard/dashboard.fxml`
- `MEMBERS` $\rightarrow$ `/fxml/member/member.fxml`
- `MEMBER_REGISTRATION` $\rightarrow$ `/fxml/member/member_registration.fxml`
- `PROPERTIES` $\rightarrow$ `/fxml/property/property.fxml`
- `FINANCE` $\rightarrow$ `/fxml/finance/finance.fxml`
- `SHARES` $\rightarrow$ `/fxml/share/share.fxml`
- `SHARE_ALLOTMENT` $\rightarrow$ `/fxml/share/share-allotment.fxml`
- `ADMINISTRATION` $\rightarrow$ `/fxml/admin/administration.fxml`

---

## 6. Offline AI Copilot Architecture (RAG Engine)

`core-os/core-ai` provides a zero-cloud, offline AI assistant running via an embedded local HTTP REST API (`http://localhost:8080`):

```
User Query (JavaFX UI)
       │
       ▼
AiCopilotController.java
       │
       ▼
LocalAiEngineService.java
       ├── Fetch Society Context (Total Members, Active Share Capital, Pending Bills)
       ├── Build Prompt Template (System Persona + Bye-Law Context + User Query)
       └── HTTP POST http://localhost:8080/v1/chat/completions
               │
               ▼
   Native LLM Engine (Llama C++ GGUF Model)
               │
               ▼
   Typewriter Response Stream to JavaFX Drawer
```

---

## 7. Packaging & Launcher Distribution

The platform provides platform-native single-click launch bundles:

### 7.1 Windows Launch Script (`dist/launch-windows.bat`)
```bat
@echo off
title Launching Society Office OS...
cd /d "%~dp0.."
call mvn javafx:run -pl domain-os/society-os
pause
```

### 7.2 macOS Launch Script (`dist/launch-mac.sh`)
```bash
#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$DIR/.."
mvn javafx:run -pl domain-os/society-os
```

---

## 8. Verification & Quality Assurance Strategy

| Component | Test Approach | Command / Artifact |
| :--- | :--- | :--- |
| **Reactor Build** | Multi-Module Maven Compilation | `mvn clean install -DskipTests` |
| **Database Migrations** | Flyway Schema Validation | `flyway.validate-on-migrate: false` |
| **Bulk CSV Importer** | Unit test with multi-date formats & missing columns | `MemberControllerTest.java` |
| **Share Allotment** | Test overlap detection & sequence generator | `ShareServiceImplTest.java` |
| **UI Integration** | Manual & JavaFX TestFX Headless Execution | `mvn javafx:run -pl domain-os/society-os` |

---

## 9. Enterprise Logging Architecture (Log4j2)

The platform utilizes **Apache Log4j2** (`spring-boot-starter-log4j2`) with SLF4J abstraction. Default Spring Boot Logback logging is explicitly excluded in `pom.xml`.

### 9.1 Log Appender Topology
- **Console Appender**: Formatted standard output stream for local developer debugging.
- **Application Rolling File Appender (`logs/society-os.log`)**: Daily rolling file appender with 10 MB size limit, compression (`.log.gz`), and 30-day retention.
- **Dedicated Audit Log Appender (`logs/audit.log`)**: Isolated compliance log capturing system mutation events (Member creations, Share allotments, Financial vouchers, Document deletions) with 90-day retention.

```
                      ┌─────────────────────────────────┐
                      │    Log4j2 Logger Engine         │
                      └────────────────┬────────────────┘
                                       │
        ┌──────────────────────────────┼──────────────────────────────┐
        │                              │                              │
┌───────▼────────┐             ┌───────▼────────┐             ┌───────▼────────┐
│    Console     │             │ Application Log│             │   Audit Log    │
│   (STDOUT)     │             │`society-os.log`│             │  `audit.log`   │
└────────────────┘             └────────────────┘             └────────────────┘
```

