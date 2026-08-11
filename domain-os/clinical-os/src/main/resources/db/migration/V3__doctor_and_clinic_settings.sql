-- Migration V3: Doctor Registration and Clinic Settings

CREATE TABLE IF NOT EXISTS doctor (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    doctor_id VARCHAR(32) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100),
    license_number VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    consultation_fee DECIMAL(10,2) DEFAULT 500.00,
    active INTEGER DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS clinic_setting (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value TEXT
);

-- Seed Default Doctors
INSERT OR IGNORE INTO doctor (doctor_id, name, specialization, license_number, phone, email, consultation_fee) VALUES
('DOC-000001', 'Dr. Suresh Kumar', 'General Medicine', 'MCI-12345', '9876543210', 'suresh@clinicalos.com', 500.00),
('DOC-000002', 'Dr. Ananya Sharma', 'Pediatrics', 'MCI-67890', '9876543211', 'ananya@clinicalos.com', 600.00);

-- Seed Default Clinic Settings
INSERT OR IGNORE INTO clinic_setting (setting_key, setting_value) VALUES
('CLINIC_NAME', 'Apex Multispecialty Clinic'),
('REGISTRATION_NO', 'REG-2026-CLINIC-88'),
('ADDRESS', '123 Healthcare Boulevard, Tech City'),
('PHONE', '+91 9988776655');
