-- Migration V5: Doctor V1 Architecture and Patient Soft Delete

-- Specialty Master Data
CREATE TABLE IF NOT EXISTS specialty (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL
);

INSERT OR IGNORE INTO specialty (code, name) VALUES
('GEN_MED', 'General Medicine'),
('PEDIATRICS', 'Pediatrics'),
('CARDIOLOGY', 'Cardiology'),
('DERMATOLOGY', 'Dermatology'),
('ORTHOPEDICS', 'Orthopedics'),
('GYNECOLOGY', 'Gynecology'),
('ENT', 'ENT (Ear, Nose, Throat)'),
('OPHTHALMOLOGY', 'Ophthalmology'),
('DENTISTRY', 'Dentistry');

-- Doctor Specialty Mapping
CREATE TABLE IF NOT EXISTS doctor_specialty (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    doctor_id VARCHAR(32) NOT NULL,
    specialty_code VARCHAR(50) NOT NULL,
    is_primary INTEGER DEFAULT 1,
    FOREIGN KEY (doctor_id) REFERENCES doctor(doctor_id),
    FOREIGN KEY (specialty_code) REFERENCES specialty(code)
);

-- Doctor Clinic Fee & Assignment
CREATE TABLE IF NOT EXISTS doctor_clinic_assignment (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    doctor_id VARCHAR(32) NOT NULL,
    clinic_name VARCHAR(150) NOT NULL,
    department VARCHAR(100),
    role VARCHAR(50) DEFAULT 'Consultant',
    new_patient_fee DECIMAL(10,2) DEFAULT 500.00,
    followup_fee DECIMAL(10,2) DEFAULT 300.00,
    emergency_fee DECIMAL(10,2) DEFAULT 800.00,
    status VARCHAR(32) DEFAULT 'ACTIVE',
    FOREIGN KEY (doctor_id) REFERENCES doctor(doctor_id)
);

-- Doctor Schedule / Availability
CREATE TABLE IF NOT EXISTS doctor_schedule (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    doctor_id VARCHAR(32) NOT NULL,
    day_of_week VARCHAR(20) NOT NULL, -- MONDAY, TUESDAY, etc.
    start_time VARCHAR(20) NOT NULL,
    end_time VARCHAR(20) NOT NULL,
    slot_duration_minutes INTEGER DEFAULT 15,
    FOREIGN KEY (doctor_id) REFERENCES doctor(doctor_id)
);
