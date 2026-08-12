-- Migration V7: Seed Demo Data across all screens (Patients, Doctors, Encounters, Pharmacy, Lab, Invoices)

-- 1. Seed Patients
INSERT OR IGNORE INTO patient (patient_id, first_name, last_name, gender, dob, phone, email, address, emergency_contact_name, emergency_contact_phone, allergies, medical_history) VALUES
('PAT-2026-000001', 'Rajesh', 'Sharma', 'Male', '1985-06-15 00:00:00.000', '9876543210', 'rajesh@example.com', 'Flat 402, Sunshine Apts, Jubilee Hills, Hyderabad', 'Sunita Sharma', '9876543299', 'Penicillin', 'Hypertension since 2020'),
('PAT-2026-000002', 'Priya', 'Verma', 'Female', '1992-11-20 00:00:00.000', '9876543211', 'priya@example.com', 'Plot 12, Hitec City, Hyderabad', 'Amit Verma', '9876543298', 'Dust, Pollen', 'Asthma'),
('PAT-2026-000003', 'Vikram', 'Reddy', 'Male', '1978-03-10 00:00:00.000', '9876543212', 'vikram@example.com', 'Road No 5, Banjara Hills, Hyderabad', 'Kavita Reddy', '9876543297', 'None', 'Type 2 Diabetes'),
('PAT-2026-000004', 'Ananya', 'Gupta', 'Female', '2018-08-05 00:00:00.000', '9876543213', 'ananya@example.com', 'House 45, Gachibowli, Hyderabad', 'Suresh Gupta', '9876543296', 'Peanuts', 'None'),
('PAT-2026-000005', 'Kalyan', 'Chakravarthy', 'Male', '1965-12-01 00:00:00.000', '9876543214', 'kalyan@example.com', 'Sector 3, Madhapur, Hyderabad', 'Latha Chakravarthy', '9876543295', 'Sulfa Drugs', 'Coronary Artery Disease');

-- 2. Seed Visits & Encounters
INSERT OR IGNORE INTO visit (visit_id, patient_id, doctor_name, visit_date, visit_type, status) VALUES
('VISIT-2026-000001', 'PAT-2026-000001', 'Dr. Suresh Kumar', '2026-08-10 00:00:00.000', 'OPD', 'COMPLETED'),
('VISIT-2026-000002', 'PAT-2026-000002', 'Dr. Ananya Sharma', '2026-08-11 00:00:00.000', 'OPD', 'COMPLETED'),
('VISIT-2026-000003', 'PAT-2026-000003', 'Dr. Suresh Kumar', '2026-08-12 00:00:00.000', 'OPD', 'WAITING'),
('VISIT-2026-000004', 'PAT-2026-000004', 'Dr. Ananya Sharma', '2026-08-12 00:00:00.000', 'OPD', 'IN_CONSULTATION');

INSERT OR IGNORE INTO clinical_encounter (encounter_id, visit_id, patient_id, doctor_id, chief_complaint, systolic_bp, diastolic_bp, pulse_rate, temperature, spo2, weight_kg, height_cm, bmi, medical_history, physical_examination, clinical_assessment, icd_code, icd_description, diagnosis, status) VALUES
('ENC-2026-000001', 'VISIT-2026-000001', 'PAT-2026-000001', 'DOC-000001', 'Headache and high BP readings', 145, 95, 82, 98.4, 98, 78.5, 172.0, 26.5, 'Hypertension for 6 years', 'S1 S2 normal, chest clear, no pedal edema', 'Essential Primary Hypertension uncontrolled', 'I10', 'Essential (primary) hypertension', 'Essential Primary Hypertension', 'COMPLETED'),
('ENC-2026-000002', 'VISIT-2026-000002', 'PAT-2026-000002', 'DOC-000002', 'Fever, dry cough, and fatigue for 3 days', 118, 76, 88, 101.2, 97, 54.0, 160.0, 21.1, 'History of mild seasonal asthma', 'Bilateral ronchi present in lung fields', 'Acute Bronchitis secondary to viral infection', 'J20.9', 'Acute bronchitis, unspecified', 'Acute Viral Bronchitis', 'COMPLETED');

-- 3. Seed Pharmacy Inventory & Dispensing
INSERT OR IGNORE INTO medicine_inventory (medicine_name, batch_number, expiry_date, quantity, reorder_level, purchase_price, selling_price) VALUES
('Paracetamol 500mg', 'BATCH-PCM-01', '2027-12-31 00:00:00.000', 450, 50, 1.50, 3.00),
('Amoxicillin 500mg', 'BATCH-AMX-02', '2027-06-30 00:00:00.000', 200, 30, 4.00, 8.50),
('Telmisartan 40mg', 'BATCH-TEL-03', '2028-01-15 00:00:00.000', 300, 40, 3.00, 6.00),
('Metformin 500mg', 'BATCH-MET-04', '2027-09-20 00:00:00.000', 500, 60, 2.00, 4.50),
('Atorvastatin 10mg', 'BATCH-ATO-05', '2027-11-10 00:00:00.000', 180, 25, 5.00, 11.00),
('Cetirizine 10mg', 'BATCH-CET-06', '2028-03-31 00:00:00.000', 350, 40, 1.00, 2.50),
('Azithromycin 500mg', 'BATCH-AZI-07', '2026-10-15 00:00:00.000', 15, 20, 12.00, 25.00), -- Low Stock Alert
('Montelukast 10mg', 'BATCH-MON-08', '2026-09-01 00:00:00.000', 10, 15, 6.00, 14.00); -- Low Stock Alert

INSERT OR IGNORE INTO prescription (prescription_id, visit_id, status) VALUES
('RX-2026-000001', 'VISIT-2026-000001', 'DISPENSED'),
('RX-2026-000002', 'VISIT-2026-000002', 'DISPENSED');

INSERT OR IGNORE INTO prescription_item (prescription_id, medicine_name, strength, dose, frequency, duration, instructions) VALUES
('RX-2026-000001', 'Telmisartan 40mg', '40mg', '1 tablet', 'OD Morning', '30 Days', 'After breakfast'),
('RX-2026-000002', 'Paracetamol 500mg', '500mg', '1 tablet', 'TID', '5 Days', 'After meals'),
('RX-2026-000002', 'Amoxicillin 500mg', '500mg', '1 capsule', 'BID', '5 Days', 'After food');

-- 4. Seed Lab Orders & Results
INSERT OR IGNORE INTO lab_order (order_id, visit_id, test_name, status, result, remarks) VALUES
('LAB-2026-000001', 'VISIT-2026-000001', 'Lipid Profile', 'COMPLETED', 'Total Cholesterol: 210 mg/dL (High), HDL: 45 mg/dL, LDL: 135 mg/dL, Triglycerides: 160 mg/dL', 'Borderline Hyperlipidemia. Advise dietary modifications.'),
('LAB-2026-000002', 'VISIT-2026-000002', 'Complete Blood Count (CBC)', 'COMPLETED', 'Hb: 13.8 g/dL, WBC: 11,200 /cu.mm (Mild Leukocytosis), Platelets: 2.8 Lakhs', 'Mild elevation in WBC suggestive of active infection.'),
('LAB-2026-000003', 'VISIT-2026-000003', 'Fasting Blood Glucose (FBS)', 'ORDERED', NULL, NULL);

-- 5. Seed Billing Invoices & Payments
INSERT OR IGNORE INTO invoice (invoice_id, visit_id, patient_id, consultation_fee, lab_fee, pharmacy_fee, total_amount, payment_mode, payment_status) VALUES
('INV-2026-000001', 'VISIT-2026-000001', 'PAT-2026-000001', 500.00, 800.00, 180.00, 1480.00, 'UPI', 'PAID'),
('INV-2026-000002', 'VISIT-2026-000002', 'PAT-2026-000002', 600.00, 450.00, 120.00, 1170.00, 'CASH', 'PAID'),
('INV-2026-000003', 'VISIT-2026-000003', 'PAT-2026-000003', 500.00, 300.00, 0.00, 800.00, 'CARD', 'UNPAID');

-- 6. Seed Referrals
INSERT OR IGNORE INTO referral (referral_id, patient_id, encounter_id, referring_doctor_id, target_specialty, target_hospital, target_doctor_name, reason, urgency, status) VALUES
('REF-2026-000001', 'PAT-2026-000001', 'ENC-2026-000001', 'DOC-000001', 'Cardiology', 'Apex Heart Institute', 'Dr. K. V. Rao', 'Evaluation for refractory hypertension and echo assessment', 'ROUTINE', 'PENDING');
