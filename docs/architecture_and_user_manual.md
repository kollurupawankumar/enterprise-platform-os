# Society Office OS - Architecture and User Manual

This manual describes the design architecture, database schema, module functions, and step-by-step instructions for operating Society Office OS.

---

## 1. Product Architecture

Society Office OS is a desktop application designed with a classic layered architecture, structured as follows:

```
[ JavaFX UI FXML View Layer ]
              ↓
[ Controller Presenter / Event Handlers ]
              ↓
[ Spring JPA Service Business Logic Layer ]
              ↓
[ Database Repository Data Access Layer ]
              ↓
[ SQLite Local Database Persistence ]
```

* **Persistence**: Local SQLite database located under `database/society.db`.
* **Migrations**: Executed automatically on startup via Flyway migrations inside `src/main/resources/db/migration`.
* **Security**: Password hashing utilizes BCrypt encryption.

---

## 2. Core Modules & User Guide

### A. Dashboard & Society Profile
* **Purpose**: Overview of society health, total members, share distribution, active compliance calendars, and quick statistic counts.
* **Profile Settings**: Managed via **Administration** -> *Society Profile* tab to update registration numbers, website, email, address, and physical parameters.

### B. Members & Share Registers
* **Member Registration**: Register primary members, flat designations, PAN, Aadhaar, joint owners, and nominees.
* **Share Allotments**: View share ranges, allot certificate numbers, print share certificate documents, and log certificate transfers.

### C. Governance Management
* **Meeting Schedules**: Plan SGM, AGM, or standard committee sessions, record attendee lists, and type out approved minutes.
* **Resolution Registers**: Index passed resolutions linked to meetings, proposed by / seconded by members, and track status.

### D. Operations & Finance
* **Vendor & Asset Registers**: Track asset purchase costs, AMC contracts, warranty expiries, and vendor service cards.
* **Expense Register & Ledgers**: Record payment vouchers, cash payments, bank account balances, and dynamic investment listings.

### E. Document Repository & Knowledge Base
* **Documents**: Simulated upload explorer to store physical files inside the local `documents/` workspace folder and track metadata tags.
* **Knowledge Base**: Collect decisions, SOP guidelines, FAQs, and lessons learned.

### F. Budgeting, Auditing, & Backups
* **Budget Variance**: Plan yearly allocated budgets. Expense categories will automatically aggregate actual spending from the ledger to compute remaining funds.
* **Auditor Queries**: Log observation items, double-click rows to input responses, and track resolutions.
* **Backup Wizard**: Trigger backup copies of the local database to the `backups/` folder and recover previous restore points.

---

## 3. Directory Layout

The application structure matches the standard format below:
```
SocietyOffice/
  ├── database/     # Active SQLite database (society.db)
  ├── backups/      # Restorable backup database snapshots (.db)
  ├── documents/    # Uploaded files and attachments (.txt, .pdf)
  ├── reports/      # Generated Form logs and spreadsheets
  ├── logs/         # Logging runtime traces
  └── docs/         # System design and walkthrough manuals
```
