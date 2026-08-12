-- Migration V3: Doctor Registration and Clinic Settings

CREATE TABLE IF NOT EXISTS doctor (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    doctor_id VARCHAR(32) NOT NULL UNIQUE,
    title VARCHAR(20) DEFAULT 'Dr.',
    first_name VARCHAR(50),
    middle_name VARCHAR(50),
    last_name VARCHAR(50),
    display_name VARCHAR(150),
    name VARCHAR(100) NOT NULL,
    gender VARCHAR(20),
    dob DATE,
    profile_photo_path VARCHAR(255),
    specialization VARCHAR(100),
    license_number VARCHAR(50),
    registration_authority VARCHAR(150),
    registration_state VARCHAR(100),
    registration_date DATE,
    qualification VARCHAR(255),
    phone VARCHAR(20),
    alternate_phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    pincode VARCHAR(20),
    consultation_fee DECIMAL(10,2) DEFAULT 500.00,
    lifecycle_status VARCHAR(32) DEFAULT 'ACTIVE',
    active INTEGER DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS clinic_setting (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value TEXT
);

-- Seed Default Doctors
INSERT OR IGNORE INTO doctor (doctor_id, title, first_name, last_name, display_name, name, specialization, license_number, phone, email, consultation_fee) VALUES
('DOC-000001', 'Dr.', 'Suresh', 'Kumar', 'Dr. Suresh Kumar', 'Dr. Suresh Kumar', 'General Medicine', 'MCI-12345', '9876543210', 'suresh@clinicalos.com', 500.00),
('DOC-000002', 'Dr.', 'Ananya', 'Sharma', 'Dr. Ananya Sharma', 'Dr. Ananya Sharma', 'Pediatrics', 'MCI-67890', '9876543211', 'ananya@clinicalos.com', 600.00);

-- Seed Default Clinic Settings
INSERT OR IGNORE INTO clinic_setting (setting_key, setting_value) VALUES
('CLINIC_NAME', 'Apex Multispecialty Clinic'),
('REGISTRATION_NO', 'REG-2026-CLINIC-88'),
('ADDRESS', '123 Healthcare Boulevard, Tech City'),
('PHONE', '+91 9988776655');
