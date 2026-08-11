-- Enhanced PatientOS V1 Schema with Configurable Settings

ALTER TABLE patient ADD COLUMN patient_type VARCHAR(32) DEFAULT 'NEW';
ALTER TABLE patient ADD COLUMN profile_photo_path VARCHAR(255);
ALTER TABLE patient ADD COLUMN alternate_phone VARCHAR(20);
ALTER TABLE patient ADD COLUMN address_line2 VARCHAR(200);
ALTER TABLE patient ADD COLUMN city VARCHAR(100);
ALTER TABLE patient ADD COLUMN state VARCHAR(100);
ALTER TABLE patient ADD COLUMN pincode VARCHAR(20);
ALTER TABLE patient ADD COLUMN country VARCHAR(100) DEFAULT 'India';

ALTER TABLE patient ADD COLUMN identity_type VARCHAR(50);
ALTER TABLE patient ADD COLUMN identity_number VARCHAR(100);

ALTER TABLE patient ADD COLUMN marital_status VARCHAR(32);
ALTER TABLE patient ADD COLUMN occupation VARCHAR(100);
ALTER TABLE patient ADD COLUMN nationality VARCHAR(50) DEFAULT 'Indian';
ALTER TABLE patient ADD COLUMN preferred_language VARCHAR(50) DEFAULT 'English';

ALTER TABLE patient ADD COLUMN emergency_relationship VARCHAR(50);
ALTER TABLE patient ADD COLUMN emergency_alternate_phone VARCHAR(20);

ALTER TABLE patient ADD COLUMN known_allergies_status VARCHAR(20) DEFAULT 'UNKNOWN';
ALTER TABLE patient ADD COLUMN patient_reported_alerts TEXT;

ALTER TABLE patient ADD COLUMN guardian_name VARCHAR(100);
ALTER TABLE patient ADD COLUMN guardian_relationship VARCHAR(50);
ALTER TABLE patient ADD COLUMN guardian_phone VARCHAR(20);
ALTER TABLE patient ADD COLUMN guardian_address TEXT;

ALTER TABLE patient ADD COLUMN payment_category VARCHAR(50) DEFAULT 'SELF_PAY';
ALTER TABLE patient ADD COLUMN insurance_provider VARCHAR(100);
ALTER TABLE patient ADD COLUMN policy_number VARCHAR(100);
ALTER TABLE patient ADD COLUMN member_id VARCHAR(100);

ALTER TABLE patient ADD COLUMN preferred_communication VARCHAR(50) DEFAULT 'SMS';

-- Add Configurable ID Prefix Setting in clinic_setting
INSERT OR IGNORE INTO clinic_setting (setting_key, setting_value) VALUES
('PATIENT_ID_PREFIX', 'PAT'),
('PATIENT_ID_FORMAT', 'PAT-{YYYY}-{SEQ}');
