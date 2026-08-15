-- Migration V11: Advance Doctor Appointment Slot Calendar & Queue Token System

CREATE TABLE IF NOT EXISTS appointment (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    appointment_id VARCHAR(32) NOT NULL UNIQUE,
    patient_id VARCHAR(32) NOT NULL,
    patient_name VARCHAR(150),
    mobile_number VARCHAR(20),
    doctor_id VARCHAR(50),
    doctor_name VARCHAR(150),
    appointment_date DATE NOT NULL,
    slot_time VARCHAR(20) NOT NULL,
    token_number INTEGER,
    status VARCHAR(32) DEFAULT 'BOOKED',
    notes VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
