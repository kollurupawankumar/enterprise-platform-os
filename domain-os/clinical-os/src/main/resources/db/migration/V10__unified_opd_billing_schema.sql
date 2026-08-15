-- Migration V10: Unified OPD Master Billing & Daily Cash Register Schema

ALTER TABLE invoice ADD COLUMN subtotal DECIMAL(10,2) DEFAULT 0.00;
ALTER TABLE invoice ADD COLUMN discount_amount DECIMAL(10,2) DEFAULT 0.00;
ALTER TABLE invoice ADD COLUMN tax_amount DECIMAL(10,2) DEFAULT 0.00;
ALTER TABLE invoice ADD COLUMN doctor_name VARCHAR(150);
ALTER TABLE invoice ADD COLUMN remarks VARCHAR(255);
