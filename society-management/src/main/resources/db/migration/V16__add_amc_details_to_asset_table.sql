ALTER TABLE asset ADD COLUMN amc_vendor_id INTEGER REFERENCES vendor(id);
ALTER TABLE asset ADD COLUMN amc_start_date TEXT;
ALTER TABLE asset ADD COLUMN amc_expiry_date TEXT;
ALTER TABLE asset ADD COLUMN amc_cost REAL;
ALTER TABLE asset ADD COLUMN amc_details TEXT;
