# Implementation Plan - Secondary Scope Items

This plan details the design and changes to add the remaining secondary scope items from the product scope:
1. **Document Management UI**
2. **Knowledge Base UI**
3. **Budgets & Audit Observations**
4. **Database Backup & Restore Wizard** (integrated as a tab in Core Administration)

---

## Proposed Changes

### Database & Schema Updates

#### [NEW] [V10__create_secondary_tables.sql](file:///Users/pawan/developer/society-management/src/main/resources/db/migration/V10__create_secondary_tables.sql)
Add Flyway migrations for:
* `knowledge_base` (lessons learned, best practices, SOPs, FAQs).
* `budget` (yearly budget planning vs actual spent).
* `audit_observation` (query logs, status tracking).

---

### Entity and Repository Layer

#### [NEW] [KnowledgeEntity.java](file:///Users/pawan/developer/society-management/src/main/java/com/society/knowledge/entity/KnowledgeEntity.java) & [KnowledgeRepository.java](file:///Users/pawan/developer/society-management/src/main/java/com/society/knowledge/repository/KnowledgeRepository.java)
Entity models to persist and query knowledge base items.

#### [NEW] [BudgetEntity.java](file:///Users/pawan/developer/society-management/src/main/java/com/society/finance/entity/BudgetEntity.java) & [BudgetRepository.java](file:///Users/pawan/developer/society-management/src/main/java/com/society/finance/repository/BudgetRepository.java)
Entity models to persist and aggregate budget tracking.

#### [NEW] [AuditObservationEntity.java](file:///Users/pawan/developer/society-management/src/main/java/com/society/finance/entity/AuditObservationEntity.java) & [AuditObservationRepository.java](file:///Users/pawan/developer/society-management/src/main/java/com/society/finance/repository/AuditObservationRepository.java)
Entity models to track audit logs and query resolutions.

---

### Presentation and FXML Layer

#### [MODIFY] [View.java](file:///Users/pawan/developer/society-management/src/main/java/com/society/common/navigation/View.java)
Declare new navigation views:
* `DOCUMENTS` -> `/fxml/document/document.fxml`
* `KNOWLEDGE_BASE` -> `/fxml/knowledge/knowledge.fxml`
* `BUDGET_AUDIT` -> `/fxml/finance/budget-audit.fxml`

#### [MODIFY] [main.fxml](file:///Users/pawan/developer/society-management/src/main/resources/fxml/main.fxml)
Add sidebar buttons:
* **Documents**
* **Knowledge Base**
* **Budgets & Audits**

#### [MODIFY] [MainController.java](file:///Users/pawan/developer/society-management/src/main/java/com/society/common/controller/MainController.java)
Wired sidebar click actions for the new navigation routes.

#### [NEW] [document.fxml](file:///Users/pawan/developer/society-management/src/main/resources/fxml/document/document.fxml) & [DocumentController.java](file:///Users/pawan/developer/society-management/src/main/java/com/society/document/controller/DocumentController.java)
Implement a file explorer interface supporting:
* Categories (GOVERNANCE, COMPLIANCE, FINANCE, etc.).
* File creation/upload (simulated file transfer to `documents/` folder).
* Tags, search, and delete actions.

#### [NEW] [knowledge.fxml](file:///Users/pawan/developer/society-management/src/main/resources/fxml/knowledge/knowledge.fxml) & [KnowledgeController.java](file:///Users/pawan/developer/society-management/src/main/java/com/society/knowledge/controller/KnowledgeController.java)
Knowledge center to view, add, and filter lessons learned, FAQs, and SOPs.

#### [NEW] [budget-audit.fxml](file:///Users/pawan/developer/society-management/src/main/resources/fxml/finance/budget-audit.fxml) & [BudgetAuditController.java](file:///Users/pawan/developer/society-management/src/main/java/com/society/finance/controller/BudgetAuditController.java)
A tabbed view:
* **Budgets**: Allocating yearly amounts per category vs current expenditures.
* **Audit Observations**: Listing auditor observations, tracking query responses, and closing query items.

#### [MODIFY] [administration.fxml](file:///Users/pawan/developer/society-management/src/main/resources/fxml/admin/administration.fxml) & [AdministrationController.java](file:///Users/pawan/developer/society-management/src/main/java/com/society/admin/controller/AdministrationController.java)
Add a **Backup & Restore** Tab containing:
* Trigger button for manual database backup to the `backups/` directory.
* Restore options listing previous backup timestamps.

---

## Verification Plan

### Automated Verification
* Run compilation: `mvn clean test-compile`
* Run all unit test suites: `mvn test`

### Manual Verification
* Run local application: `mvn javafx:run`
* Check sidebar links navigate cleanly to Documents, KB, and Budgets.
* Test backup trigger and verify backup files are created in the `backups/` workspace directory.
