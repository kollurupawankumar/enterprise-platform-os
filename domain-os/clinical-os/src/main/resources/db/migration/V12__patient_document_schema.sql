-- Migration V12: Clinical EMR Patient Document Uploads & Medical Records Schema

CREATE TABLE IF NOT EXISTS patient_document (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    document_id VARCHAR(32) NOT NULL UNIQUE,
    patient_id VARCHAR(32) NOT NULL,
    visit_id VARCHAR(32),
    document_name VARCHAR(150) NOT NULL,
    document_type VARCHAR(50) NOT NULL, -- X-RAY, MRI, USG, EXTERNAL_LAB_REPORT, DISCHARGE_SUMMARY, OTHER
    file_path VARCHAR(255) NOT NULL,
    file_size VARCHAR(32),
    uploaded_by VARCHAR(100),
    remarks VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
