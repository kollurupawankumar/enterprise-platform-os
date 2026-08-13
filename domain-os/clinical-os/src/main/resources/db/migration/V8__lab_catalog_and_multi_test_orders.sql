-- Migration V8: Diagnostic Test Catalog, Per-Test Pricing, and Multi-Test Line Items

-- Alter existing lab_order table to support billing and doctor assignment
ALTER TABLE lab_order ADD COLUMN doctor_name VARCHAR(150);
ALTER TABLE lab_order ADD COLUMN total_amount DECIMAL(10,2) DEFAULT 0.00;
ALTER TABLE lab_order ADD COLUMN payment_status VARCHAR(32) DEFAULT 'UNPAID';
ALTER TABLE lab_order ADD COLUMN payment_mode VARCHAR(32) DEFAULT 'CASH';

CREATE TABLE IF NOT EXISTS lab_test_catalog (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    test_code VARCHAR(32) NOT NULL UNIQUE,
    test_name VARCHAR(150) NOT NULL,
    category VARCHAR(64) NOT NULL,
    sample_type VARCHAR(64),
    unit_price DECIMAL(10,2) NOT NULL,
    normal_range VARCHAR(100),
    units VARCHAR(32),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Seed Default Pre-seeded Diagnostic Test Catalog
INSERT OR IGNORE INTO lab_test_catalog (test_code, test_name, category, sample_type, unit_price, normal_range, units) VALUES
('CBC-101', 'Complete Blood Count (CBC)', 'Hematology', 'EDTA Blood', 350.00, '13.5 - 17.5', 'g/dL'),
('LIP-201', 'Lipid Profile', 'Biochemistry', 'Serum', 650.00, '< 200', 'mg/dL'),
('THY-301', 'Thyroid Function Test (T3, T4, TSH)', 'Endocrinology', 'Serum', 800.00, '0.4 - 4.0', 'mIU/L'),
('HBA-401', 'HbA1c (Glycated Hemoglobin)', 'Biochemistry', 'EDTA Blood', 500.00, '4.0 - 5.6', '%'),
('LFT-501', 'Liver Function Test (LFT)', 'Biochemistry', 'Serum', 750.00, '7 - 56', 'U/L'),
('KFT-601', 'Kidney Function Test (KFT / RFT)', 'Biochemistry', 'Serum', 700.00, '0.6 - 1.2', 'mg/dL'),
('URN-701', 'Urine Routine & Microscopy', 'Pathology', 'Urine', 250.00, 'Clear / Normal', '--'),
('BSL-801', 'Fasting Blood Sugar (FBS)', 'Biochemistry', 'Fluoride Plasma', 150.00, '70 - 99', 'mg/dL');

CREATE TABLE IF NOT EXISTS lab_order_item (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id VARCHAR(32) NOT NULL,
    test_code VARCHAR(32) NOT NULL,
    test_name VARCHAR(150) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    status VARCHAR(32) DEFAULT 'ORDERED',
    result_value VARCHAR(150),
    normal_range VARCHAR(100),
    units VARCHAR(32),
    flag VARCHAR(16) DEFAULT 'NORMAL',
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES lab_order(order_id)
);

