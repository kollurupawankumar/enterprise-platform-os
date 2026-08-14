-- Migration V9: Pharmacy POS, OPD Prescription Dispensing & Stock Catalog

ALTER TABLE medicine_inventory ADD COLUMN medicine_code VARCHAR(32);
ALTER TABLE medicine_inventory ADD COLUMN category VARCHAR(64) DEFAULT 'General';
ALTER TABLE medicine_inventory ADD COLUMN mrp DECIMAL(10,2) DEFAULT 0.00;

-- Seed Default Pre-seeded Common Medications
INSERT OR IGNORE INTO medicine_inventory (medicine_code, medicine_name, category, batch_number, expiry_date, purchase_price, selling_price, mrp, quantity, reorder_level, supplier) VALUES
('MED-101', 'Paracetamol 500mg', 'Analgesics', 'PCM-2026-A1', '2027-12-31 00:00:00.000', 2.00, 3.50, 5.00, 500, 20, 'GlaxoSmithKline'),
('MED-102', 'Amoxicillin 500mg', 'Antibiotics', 'AMX-2026-B2', '2027-08-31 00:00:00.000', 5.50, 8.50, 12.00, 250, 25, 'Cipla Ltd'),
('MED-103', 'Azithromycin 500mg', 'Antibiotics', 'AZI-2026-C3', '2027-10-31 00:00:00.000', 12.00, 18.00, 25.00, 150, 15, 'Sun Pharma'),
('MED-104', 'Pantoprazole 40mg', 'Gastrointestinal', 'PAN-2026-D4', '2027-11-30 00:00:00.000', 4.00, 6.50, 9.00, 300, 30, 'Alkem Labs'),
('MED-105', 'Cetirizine 10mg', 'Antihistamines', 'CET-2026-E5', '2028-01-31 00:00:00.000', 1.50, 2.50, 4.00, 400, 20, 'Dr Reddys'),
('MED-106', 'Metformin 500mg', 'Antidiabetic', 'MET-2026-F6', '2027-09-30 00:00:00.000', 2.50, 4.00, 6.00, 600, 50, 'Lupin Ltd'),
('MED-107', 'Ibuprofen 400mg', 'Anti-inflammatory', 'IBU-2026-G7', '2027-06-30 00:00:00.000', 2.00, 3.00, 5.00, 200, 20, 'Abbott India'),
('MED-108', 'Multivitamin & Zinc', 'Supplements', 'MTV-2026-H8', '2028-03-31 00:00:00.000', 3.00, 5.00, 8.00, 350, 30, 'Mankind Pharma');


CREATE TABLE IF NOT EXISTS pharmacy_invoice (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    invoice_id VARCHAR(32) NOT NULL UNIQUE,
    visit_id VARCHAR(32),
    patient_id VARCHAR(32),
    doctor_name VARCHAR(150),
    subtotal DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    tax_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    payment_status VARCHAR(32) DEFAULT 'PAID',
    payment_mode VARCHAR(32) DEFAULT 'CASH',
    status VARCHAR(32) DEFAULT 'DISPENSED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pharmacy_invoice_item (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    invoice_id VARCHAR(32) NOT NULL,
    medicine_code VARCHAR(32),
    medicine_name VARCHAR(150) NOT NULL,
    batch_number VARCHAR(50) NOT NULL,
    expiry_date VARCHAR(32),
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    line_total DECIMAL(10,2) NOT NULL,
    dosage_instruction VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (invoice_id) REFERENCES pharmacy_invoice(invoice_id)
);
