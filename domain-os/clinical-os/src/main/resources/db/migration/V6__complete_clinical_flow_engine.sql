-- Migration V6: Complete Clinical Flow Engine (Referral Tracking System)

-- Referral Tracking Table
CREATE TABLE IF NOT EXISTS referral (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    referral_id VARCHAR(32) NOT NULL UNIQUE,
    patient_id VARCHAR(32) NOT NULL,
    encounter_id VARCHAR(32) NOT NULL,
    referring_doctor_id VARCHAR(32) NOT NULL,
    target_specialty VARCHAR(100),
    target_hospital VARCHAR(150),
    target_doctor_name VARCHAR(100),
    reason TEXT,
    urgency VARCHAR(20) DEFAULT 'ROUTINE',
    status VARCHAR(32) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patient(patient_id),
    FOREIGN KEY (encounter_id) REFERENCES clinical_encounter(encounter_id)
);
