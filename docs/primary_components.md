# Primary Components Documentation - Society Office OS

This document details the primary components implemented in Phase 2, covering the core registers, governance, operations, finance, search, and layout utilities of the housing society management system.

---

## 1. Membership & Property Registers

### A. Member Management
* **Database Tables**: `member`, `joint_owner`, `nominee`.
* **Details**: Maps first name, last name, email, mobile, Aadhaar, and PAN. Supports multiple joint owners and designated nominees per membership profile.
* **Status States**: `ACTIVE`, `SUSPENDED`, `INACTIVE`.

### B. Property Register
* **Database Tables**: `property`, `ownership_history`.
* **Details**: Stores property number, block code, type (Apartment / Villa), and current owner link. Tracks historical owner transitions whenever a property is sold/transferred.

---

## 2. Share Register & Certificate Printing

### A. Share Capital Registry
* **Database Tables**: `share_certificate`, `share_transfer_history`.
* **Details**: Manages certificate numbers, share ranges (from number to to number), face value per share, and date of issue.
* **Certificate Printing**: Maps absolute overlay label coordinates on top of the `membership_cum_share_certificate.jpeg` image to print formatted cooperative housing society share certifications automatically in `dd-MM-yyyy` date formats.

---

## 3. Governance Module

### A. Meeting Register
* **Database Tables**: `meeting`.
* **Details**: Organizes meeting titles, categories (AGM, SGM, Committee), agendas, venues, dates, and final written minutes of the session.

### B. Resolution Register
* **Database Tables**: `resolution`.
* **Details**: Captures resolution numbers, subjects, descriptions, related meeting links, proposers, seconders, and approval status.

### C. Compliance & Tasks
* **Database Tables**: `compliance`, `task`.
* **Details**: Tracks statutory timelines (Fire NOC, Lift licenses, Insurance renewals) and links tasks to committee owners with status tracking.

---

## 4. Operations Module

### A. Asset Management
* **Database Tables**: `asset`.
* **Details**: Lists physical capital assets, category types, serial numbers, purchase cost/date, and warranty expirations.

### B. Vendor & Maintenance Projects
* **Database Tables**: `vendor`, `project`.
* **Details**: Tracks qualified contractors and manages repair projects from initial proposal through quotes, execution timelines, and completion reports.

---

## 5. Finance Module

### A. Bank Account Ledger
* **Database Tables**: `bank_account`.
* **Details**: Manages active bank account names, IFSC codes, branch addresses, and running balance ledger entries.

### B. Expense & Investment Tracker
* **Database Tables**: `expense`, `investment`.
* **Details**: Registers daily cash/bank payment vouchers, payee info, category tags, and fixed deposit/investment principal amounts with maturity dates.

---

## 6. Universal Search

* **Details**: A centralized query field that filters and aggregates records across Members, Properties, Meetings, Documents, Projects, Assets, Vendors, Knowledge Notes, and Resolutions.
