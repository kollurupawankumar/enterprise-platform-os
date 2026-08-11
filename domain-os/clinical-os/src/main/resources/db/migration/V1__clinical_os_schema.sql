-- ClinicalOS Initial Database Schema

CREATE TABLE IF NOT EXISTS patient (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    patient_id VARCHAR(32) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    gender VARCHAR(20),
    dob DATE,
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    allergies TEXT,
    medical_history TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS visit (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    visit_id VARCHAR(32) NOT NULL UNIQUE,
    patient_id VARCHAR(32) NOT NULL,
    doctor_name VARCHAR(100) NOT NULL,
    visit_date DATE NOT NULL,
    visit_type VARCHAR(32) DEFAULT 'OPD',
    status VARCHAR(32) DEFAULT 'WAITING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patient(patient_id)
);

CREATE TABLE IF NOT EXISTS clinical_encounter (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    visit_id VARCHAR(32) NOT NULL UNIQUE,
    chief_complaints TEXT,
    temperature VARCHAR(20),
    bp VARCHAR(20),
    pulse VARCHAR(20),
    spo2 VARCHAR(20),
    weight VARCHAR(20),
    height VARCHAR(20),
    clinical_notes TEXT,
    diagnosis TEXT,
    icd_code VARCHAR(32),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (visit_id) REFERENCES visit(visit_id)
);

CREATE TABLE IF NOT EXISTS prescription (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    prescription_id VARCHAR(32) NOT NULL UNIQUE,
    visit_id VARCHAR(32) NOT NULL,
    status VARCHAR(32) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (visit_id) REFERENCES visit(visit_id)
);

CREATE TABLE IF NOT EXISTS prescription_item (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    prescription_id VARCHAR(32) NOT NULL,
    medicine_name VARCHAR(150) NOT NULL,
    strength VARCHAR(50),
    dose VARCHAR(50),
    frequency VARCHAR(50),
    duration VARCHAR(50),
    instructions TEXT,
    FOREIGN KEY (prescription_id) REFERENCES prescription(prescription_id)
);

CREATE TABLE IF NOT EXISTS lab_order (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id VARCHAR(32) NOT NULL UNIQUE,
    visit_id VARCHAR(32) NOT NULL,
    test_name VARCHAR(150) NOT NULL,
    status VARCHAR(32) DEFAULT 'ORDERED', -- ORDERED, SAMPLE_COLLECTED, IN_PROCESS, COMPLETED, REVIEWED
    result TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (visit_id) REFERENCES visit(visit_id)
);

CREATE TABLE IF NOT EXISTS medicine_inventory (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    medicine_name VARCHAR(150) NOT NULL,
    batch_number VARCHAR(50) NOT NULL,
    expiry_date DATE,
    purchase_price DECIMAL(10,2),
    selling_price DECIMAL(10,2),
    quantity INTEGER DEFAULT 0,
    supplier VARCHAR(100),
    location VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS invoice (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    invoice_id VARCHAR(32) NOT NULL UNIQUE,
    visit_id VARCHAR(32) NOT NULL,
    patient_id VARCHAR(32) NOT NULL,
    consultation_fee DECIMAL(10,2) DEFAULT 0,
    lab_fee DECIMAL(10,2) DEFAULT 0,
    pharmacy_fee DECIMAL(10,2) DEFAULT 0,
    total_amount DECIMAL(10,2) DEFAULT 0,
    payment_status VARCHAR(32) DEFAULT 'UNPAID', -- UNPAID, PAID, PARTIAL
    payment_mode VARCHAR(32), -- CASH, UPI, CARD, BANK_TRANSFER
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (visit_id) REFERENCES visit(visit_id),
    FOREIGN KEY (patient_id) REFERENCES patient(patient_id)
);

CREATE TABLE IF NOT EXISTS followup (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    visit_id VARCHAR(32) NOT NULL,
    patient_id VARCHAR(32) NOT NULL,
    doctor_name VARCHAR(100) NOT NULL,
    followup_date DATE NOT NULL,
    reason TEXT,
    status VARCHAR(32) DEFAULT 'SCHEDULED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (visit_id) REFERENCES visit(visit_id),
    FOREIGN KEY (patient_id) REFERENCES patient(patient_id)
);

CREATE TABLE IF NOT EXISTS clinical_audit_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    actor VARCHAR(100) NOT NULL,
    action VARCHAR(100) NOT NULL,
    details TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
