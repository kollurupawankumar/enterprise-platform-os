# Walkthrough - Secondary Scope Features

This walkthrough details the implementations completed to cover 100% of the target product scope for Society Office OS:

---

## 1. Database Schema Additions
Created Flyway migration [V10__create_secondary_tables.sql](file:///Users/pawan/developer/society-management/src/main/resources/db/migration/V10__create_secondary_tables.sql):
* **`knowledge_base`**: Stores title, category, content, author, and timestamp columns.
* **`budget`**: Year, category, allocated_amount, actual_spent, and timestamp columns.
* **`audit_observation`**: observation_date, auditor_name, observation_text, query_tracker (for response details), status, and timestamp columns.

---

## 2. Model & Repository Declarations
* **Knowledge**: Added [KnowledgeEntity.java](file:///Users/pawan/developer/society-management/src/main/java/com/society/knowledge/entity/KnowledgeEntity.java) and [KnowledgeRepository.java](file:///Users/pawan/developer/society-management/src/main/java/com/society/knowledge/repository/KnowledgeRepository.java).
* **Budget**: Added [BudgetEntity.java](file:///Users/pawan/developer/society-management/src/main/java/com/society/finance/entity/BudgetEntity.java) and [BudgetRepository.java](file:///Users/pawan/developer/society-management/src/main/java/com/society/finance/repository/BudgetRepository.java).
* **Audit Tracker**: Added [AuditObservationEntity.java](file:///Users/pawan/developer/society-management/src/main/java/com/society/finance/entity/AuditObservationEntity.java) and [AuditObservationRepository.java](file:///Users/pawan/developer/society-management/src/main/java/com/society/finance/repository/AuditObservationRepository.java).

---

## 3. UI Views & Controllers

### A. Document Explorer UI
* **View**: Created [document.fxml](file:///Users/pawan/developer/society-management/src/main/resources/fxml/document/document.fxml).
* **Controller**: Created [DocumentController.java](file:///Users/pawan/developer/society-management/src/main/java/com/society/document/controller/DocumentController.java) managing category and tags filtering, file size conversions, deleting documents, and simulating dummy text file generation inside the local `documents/` folder.

### B. Knowledge Base UI
* **View**: Created [knowledge.fxml](file:///Users/pawan/developer/society-management/src/main/resources/fxml/knowledge/knowledge.fxml).
* **Controller**: Created [KnowledgeController.java](file:///Users/pawan/developer/society-management/src/main/java/com/society/knowledge/controller/KnowledgeController.java) allowing filter-by-category (SOPs, FAQs, Decisions, Lessons Learned), split panel viewer, and input dialog to add new knowledge notes.

### C. Budgets & Audits UI
* **View**: Created [budget-audit.fxml](file:///Users/pawan/developer/society-management/src/main/resources/fxml/finance/budget-audit.fxml).
* **Controller**: Created [BudgetAuditController.java](file:///Users/pawan/developer/society-management/src/main/java/com/society/finance/controller/BudgetAuditController.java) featuring:
  * **Budgets**: Allocating yearly amounts per category. Dynamically sums actual spent figures from matching category expenses inside `ExpenseRepository` automatically.
  * **Audits**: Listing observations. Double-clicking any observation opens a modal to submit query responses and change the resolution status.

### D. Backup & Restore Wizard
* **View**: Updated [administration.fxml](file:///Users/pawan/developer/society-management/src/main/resources/fxml/admin/administration.fxml) with a tabbed view.
* **Controller**: Enhanced [AdministrationController.java](file:///Users/pawan/developer/society-management/src/main/java/com/society/admin/controller/AdministrationController.java) implementing local copy triggers that save a snapshot of the SQLite database inside `backups/` and restore points that list and recover previous snapshots.

---

## 4. Main Sidebar Updates
* Added navigation routes and buttons in [main.fxml](file:///Users/pawan/developer/society-management/src/main/resources/fxml/main.fxml) and [MainController.java](file:///Users/pawan/developer/society-management/src/main/java/com/society/common/controller/MainController.java).
* Removed startup disable blockers on `reportsButton` and `administrationButton` to enable full access.
