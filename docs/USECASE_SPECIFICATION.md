# USE-CASE SPECIFICATION DOCUMENT
## Enterprise Platform OS — Society Office OS Domain Module
**Document Version**: 2.0  
**Author**: Antigravity Assistant & Engineering Team  
**Target Platform**: Desktop Enterprise Application (JavaFX 21 + Spring Boot 3.5.4 + SQLite)  

---

### Executive Overview & Product Context
**Society Office OS** is a specialized, offline-first desktop enterprise application built specifically for **Housing Society Office Management Committees, Accountants, Administrative Officers, and Statutory Auditors**. 

Unlike consumer gate-security applications (MyGate, NoBrokerHood), **Society Office OS** focuses strictly on **formal cooperative governance, statutory legal compliance under Cooperative Housing Society Bye-Laws, share capital administration, financial accounting, asset management, and offline local AI assistance**.

---

## 1. Actor Catalog & Authorization Matrix

### 1.1 Primary Actors
1. **System Administrator (ADMIN)**: Responsible for initial society setup, system configuration, database backup/restore, user management, and audit log auditing.
2. **Society Secretary (SECRETARY)**: Handles member onboarding, share allotment, share transfers, meeting governance, minutes recording, and statutory nomination registers.
3. **Society Treasurer / Accountant (TREASURER)**: Manages society finances, voucher recording, bank accounts, Fixed Deposit portfolios, maintenance billing, and trial balance reports.
4. **Statutory Auditor (AUDITOR)**: Read-only compliance actor inspecting audit trails, share registers, asset service logs, and financial ledgers.
5. **AI Copilot (LOCAL_AI)**: Offline embedded LLM assisting administrative staff with Bye-Law queries, RAG data lookup, and report generation.

---

## 2. Core Functional Use Cases

---

### UC-01: Dynamic Society Configuration & Numbering Scheme Setup
* **Actor**: Administrator
* **Pre-conditions**: System installed and initialized.
* **Goal**: Define society registration details and dynamic statutory numbering formats for Members, Memberships, and Share Certificates.

#### Main Success Scenario (MSS):
1. Administrator navigates to **Administration $\rightarrow$ Society Profile**.
2. System loads existing profile details including Society Name, Registration Number, Financial Year Start Month, Logo, and Seal.
3. Administrator configures dynamic numbering format patterns:
   - **Member Number Format**: e.g., `MEM-{SEQ}`
   - **Membership Number Format**: e.g., `SSTS/{YEAR}/{SEQ}`
   - **Share Certificate Format**: e.g., `SC/{YEAR}/{SEQ}`
4. System displays live example previews using placeholders (`{YEAR}`, `{YY}`, `{SEQ}`, `{PREFIX}`).
5. Administrator clicks **Save Details**.
6. System validates inputs, persists patterns to `society` table, and logs audit trail.

#### Exception Flows:
- **EF-01A (Invalid Placeholder Syntax)**: System displays error alert indicating unrecognized placeholders and restores last valid pattern.

---

### UC-02: Member Registration & Statutory Nomination Entry (Form J / Form K)
* **Actor**: Secretary / Administrator
* **Pre-conditions**: Society profile configured.
* **Goal**: Register a new member, assign auto-generated Member & Membership numbers, and record statutory nominees.

#### Main Success Scenario (MSS):
1. Secretary navigates to **Members $\rightarrow$ New Member**.
2. System auto-generates next sequential **Member No** (`MEM-00007`) and **Membership No** (`SSTS/2026/00007`) based on configured society patterns.
3. Secretary enters Personal Details (First Name, Middle Name, Last Name, Gender, DOB, Occupation, Aadhaar, PAN).
4. Secretary enters Contact & Address Details (Mobile, Email, Permanent Address, Correspondence Address).
5. Secretary enters Admission Details (Admission Date, Resolution No, Resolution Date, Member Type).
6. Secretary adds Joint Owners (if applicable) with Aadhaar and PAN details.
7. Secretary adds Nominees (Form K compliance) specifying Nominee Name, Relationship, and Percentage Share (must sum to 100%).
8. Secretary clicks **Save Member**.
9. System validates Aadhaar (12 digits), PAN format, mobile number, saves record, and updates system counters.

#### Exception Flows:
- **EF-02A (Duplicate PAN/Aadhaar)**: System blocks saving and alerts secretary of existing member record.
- **EF-02B (Nominee Share % Invalid)**: System alerts if total nominee share percentage $\neq$ 100%.

---

### UC-03: Resilient Bulk Member Import from CSV / Excel
* **Actor**: Secretary / Accountant
* **Pre-conditions**: Prepared CSV file from legacy software or spreadsheet.
* **Goal**: Import hundreds of member records in bulk with automatic header mapping and date formatting tolerance.

#### Main Success Scenario (MSS):
1. Secretary navigates to **Members $\rightarrow$ Bulk Import CSV**.
2. Secretary selects a CSV file from local disk.
3. System reads header row and performs **Header-Agnostic Column Matching** (maps `First Name`, `DOB`, `Mobile`, `Admission Date`, etc., regardless of column index).
4. For each row:
   - System parses flexible date formats (`DD/MM/YY`, `DD/MM/YYYY`, `DD-MM-YYYY`, `YYYY-MM-DD`).
   - If `Member No` or `Membership No` is missing in CSV, system auto-generates them using society's custom numbering formats.
   - Validates required fields and registers member in database.
5. System displays **Bulk Import Summary Dialog**:
   - Total Successfully Imported Count.
   - Total Skipped / Failed Count with detailed line-by-line error list.
6. System refreshes member table.

#### Exception Flows:
- **EF-03A (Empty File / Invalid CSV Header)**: System displays error alert: *"The selected CSV file is empty or missing mandatory header columns."*

---

### UC-04: Smart Share Certificate Allotment (Form I Register Compliance)
* **Actor**: Secretary
* **Pre-conditions**: Member registered in system.
* **Goal**: Issue a statutory Share Certificate with auto-detected distinctive share ranges.

#### Main Success Scenario (MSS):
1. Secretary navigates to **Shares $\rightarrow$ Issue Share Certificate**.
2. System auto-generates next **Certificate Number** (`SC/2026/00001`).
3. Secretary selects Target Member from dropdown.
4. System queries database for highest allotted `to_share_number` (e.g. `100`) and automatically sets **`From Share No: 101`** (Read-only/Auto).
5. Secretary enters **`Number of Shares`** (default `10`).
6. System automatically calculates **`To Share No: 110`** and **`Total Share Amount: ₹ 500.00`** (at face value ₹50/share).
7. Secretary selects Issue Date and clicks **Issue Certificate**.
8. System checks for share range overlap in `share_certificate` table.
9. System saves certificate as `ACTIVE`, updates member share holdings, and logs audit record.

#### Exception Flows:
- **EF-04A (Share Range Overlap)**: System detects range collision with existing active shares and prevents saving with an explicit error alert.

---

### UC-05: Share Certificate Transfer & Ownership History Tracking
* **Actor**: Secretary
* **Pre-conditions**: Active share certificate exists.
* **Goal**: Transfer an existing share certificate from selling member to purchasing member upon flat resale.

#### Main Success Scenario (MSS):
1. Secretary navigates to **Shares**, selects an active Share Certificate, and clicks **Transfer Shares**.
2. System opens Transfer Dialog showing Current Certificate Details, Current Owner, Share Range (`101 - 110`), and Total Amount.
3. Secretary selects Target New Member from dropdown.
4. Secretary enters Transfer Fee (e.g. `₹ 500.00`), Transfer Date, and Resolution Remarks.
5. Secretary clicks **Confirm Transfer**.
6. System creates a `ShareTransferHistoryEntity` record, updates certificate owner to New Member, and flags old member transfer history.

#### Exception Flows:
- **EF-05A (Transfer to Same Member)**: System alerts *"Cannot transfer shares to the same member."*

---

### UC-06: Property, Ownership & Flat History Register
* **Actor**: Secretary / Administrator
* **Pre-conditions**: Society layout registered.
* **Goal**: Manage wings, flat numbers, area (sq ft), maintenance rates, and ownership assignments.

#### Main Success Scenario (MSS):
1. Secretary navigates to **Properties**.
2. System displays property list categorized by Wing, Flat No, Floor, Flat Type (1BHK/2BHK/3BHK/Penthouse), Area (sq.ft), and Current Owner.
3. Secretary clicks **Add Property** or selects existing flat to change ownership.
4. System records owner history log ensuring full auditability of property transfers over time.

---

### UC-07: Financial Voucher & Expenditure Management
* **Actor**: Treasurer / Accountant
* **Pre-conditions**: Expense categories defined.
* **Goal**: Record society expenditures, payment modes, approval authority, and attach scanned bill receipts.

#### Main Success Scenario (MSS):
1. Treasurer navigates to **Finance $\rightarrow$ Expense Register**.
2. Treasurer clicks **Record Expense**.
3. Treasurer inputs Voucher Date, Payee Name, Expenditure Category (e.g., *Security Charges, Lift Maintenance, Electricity Bill, Gardening*), Amount (₹), Payment Mode (Cheque/NEFT/UPI/Cash), Reference/Transaction Number, and Approval Authority.
4. Treasurer clicks **Attach Receipt** to select a scanned PDF/image bill.
5. System copies receipt file to local `uploads/receipts/` storage directory.
6. Treasurer clicks **Save Voucher**.
7. System persists voucher, updates category expense totals, and writes to audit log.

---

### UC-08: Bank Accounts & Fixed Deposit (FD) Portfolio Management
* **Actor**: Treasurer
* **Pre-conditions**: Active financial accounts exist.
* **Goal**: Track society bank balances, Fixed Deposit investments, interest rates, and maturity dates.

#### Main Success Scenario (MSS):
1. Treasurer navigates to **Finance $\rightarrow$ Bank Accounts & Fixed Deposits**.
2. System displays list of Bank Accounts (Bank Name, Account No, IFSC Code, Branch) and FD Portfolio (FD Ref No, Institution, Principal Amount, Interest Rate %, Maturity Date).
3. Treasurer clicks **Record FD** to enter a new Fixed Deposit investment.
4. System automatically computes maturity value and calculates total portfolio value displayed in dashboard card.

---

### UC-09: Asset Registry, AMC Renewal & Service Maintenance Logging
* **Actor**: Operations Officer / Secretary
* **Pre-conditions**: Assets registered.
* **Goal**: Maintain society equipment assets (Lifts, Generators, Water Pumps, CCTV System, Solar Panels), AMC vendor contracts, and service logs.

#### Main Success Scenario (MSS):
1. User navigates to **Operations $\rightarrow$ Assets & AMC**.
2. User selects an asset (e.g., *Generator Set 125 KVA*).
3. System displays Asset Serial No, Purchase Date, AMC Vendor Contact, AMC Expiry Date, and Warranty Status.
4. User clicks **Log Maintenance Service** to record service date, technician details, cost, and service summary.
5. System saves service log in `asset_service_log` table with audit timestamps (`created_by`, `created_at`).

---

### UC-10: Meeting Governance & Committee Minutes Recording
* **Actor**: Secretary
* **Pre-conditions**: Managing Committee members designated.
* **Goal**: Schedule Managing Committee Meetings / AGMs, record attendance, and log official meeting minutes/resolutions.

#### Main Success Scenario (MSS):
1. Secretary navigates to **Governance $\rightarrow$ Meetings**.
2. Secretary clicks **Schedule Meeting** (Title, Type: AGM/SGM/Committee, Date, Time, Venue/Online Link, Agenda).
3. After meeting execution, Secretary opens meeting record and clicks **Record Minutes & Attendance**.
4. System records present members, resolution text, and marks meeting status as `COMPLETED`.

---

### UC-11: Statutory Document Vault & Digital Archive
* **Actor**: Secretary / Administrator
* **Pre-conditions**: Scanned files available.
* **Goal**: Archive original legal documents (Conveyance Deed, Approved Layout Plan, Registration Certificate, Building Fire Safety NOC) securely on local storage.

#### Main Success Scenario (MSS):
1. User navigates to **Documents**.
2. User uploads file specifying Category (*Statutory, Building Plans, Legal, Financial Audits*), Document Title, Reference Number, and Expiry Date.
3. System saves file under encrypted local storage path and indexes record for instant retrieval.

---

### UC-12: Offline Local AI Copilot Assistance (RAG Engine)
* **Actor**: Any Authorized User
* **Pre-conditions**: Native `llama-server` process running locally.
* **Goal**: Interact with offline AI assistant to query society data, Bye-Law rules, or draft official letters without internet connectivity.

#### Main Success Scenario (MSS):
1. User clicks **🤖 AI Copilot** button on the bottom left sidebar.
2. System opens right drawer panel displaying conversation history.
3. User types a query (e.g. *"Summarize total active members and share capital"* or *"Draft a notice for upcoming AGM"*).
4. System constructs RAG context payload from SQLite database and posts to local LLM REST endpoint (`http://localhost:8080/v1/chat/completions`).
5. Local AI processes context offline and returns answer typewriter-style in chat panel.

---

### UC-13: Database Backup, Snapshot & Disaster Recovery
* **Actor**: Administrator
* **Pre-conditions**: Local disk space available.
* **Goal**: Create manual or scheduled ZIP snapshots of `society.db` and restore database state when required.

#### Main Success Scenario (MSS):
1. Administrator navigates to **Administration $\rightarrow$ Backup & Restore Settings**.
2. Administrator clicks **Trigger Manual Backup**.
3. System uses `core-config` `BackupService` to create a timestamped backup copy (`backups/society_backup_20260809_120000.db`).
4. System updates list of available restore points.
5. In case of corruption, Administrator selects a backup file and clicks **Restore Selected Point**. System confirms overwrite and restores database.

---

### UC-14: Universal Search Engine Across Platform
* **Actor**: Any Authorized User
* **Pre-conditions**: Data present in system.
* **Goal**: Search globally across Members, Properties, Share Certificates, Assets, and Vouchers from a single search box.

#### Main Success Scenario (MSS):
1. User navigates to **Universal Search** or types in top header search bar.
2. User enters search term (e.g. *"Rahul"*, *"SC-00001"*, *"Generator"*, or *"Flat 101"*).
3. `core-search` `UniversalSearchEngine` queries all registered domain providers in parallel.
4. System displays grouped search results with direct navigation buttons to jump to target module screens.

---

## 3. Non-Functional & Technical Requirements Summary

| Requirement Category | Metric / Specification |
| :--- | :--- |
| **Deployment Architecture** | Single-user / Local Network Enterprise Desktop Application (JavaFX 21 + Spring Boot 3.5.4 embedded Tomcat). |
| **Database Engine** | Embedded SQLite (`society.db`) with Flyway schema migration engine. |
| **Privacy & Security** | 100% Offline-First. Zero cloud data leakage. Password hashing via BCrypt. |
| **Modular Core OS Platform** | Architectural separation into 10 reusable `core-os` modules and decoupled `domain-os/society-os`. |
| **Performance** | UI responsiveness $< 100\text{ms}$ for database queries. Local AI response $< 3\text{s}$ using GGML quantised GGUF model. |
