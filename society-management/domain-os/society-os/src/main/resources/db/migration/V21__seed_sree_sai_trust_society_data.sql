DELETE FROM asset_service_log;
DELETE FROM asset;
DELETE FROM facility;
DELETE FROM society_staff;
DELETE FROM vendor;
DELETE FROM expense;
DELETE FROM bank_account;
DELETE FROM investment;
DELETE FROM meeting_attendance;
DELETE FROM resolution;
DELETE FROM meeting;
DELETE FROM managing_committee;
DELETE FROM document;
DELETE FROM share_certificate;
DELETE FROM share_transfer_history;
DELETE FROM property;
DELETE FROM member;
DELETE FROM society;

-- 1. SOCIETY PROFILE
INSERT INTO society (id, name, registration_number, short_name, email, phone, website, address_line1, address_line2, financial_year_start_month, active)
VALUES (1, 'Sree Sai Trust Co-operative Housing Society Ltd.', 'REG/TG/HYD/2024/09812', 'Sree Sai Trust Society', 'contact@sreesaitrustsociety.in', '+91 40 2988 7766', 'www.sreesaitrustsociety.in', 'Sy No 142/A & 143, Financial District, Gachibowli', 'Hyderabad, Telangana - 500032', 'APRIL', 1);

-- 2. VENDORS
INSERT INTO vendor (id, name, category, phone, contact_person, email, address, active, created_at, updated_at) VALUES
(1, 'Schindler Elevators India Pvt Ltd', 'ELEVATOR', '+91 40 4455 6677', 'Rajesh Varma', 'support.hyd@schindler.com', 'Plot 42, Hitec City, Hyderabad, Telangana', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Cummins India Genset Services', 'GENERATOR', '+91 40 6688 9900', 'Venkatesh Rao', 'service.hyd@cummins.com', 'Industrial Estate, Sanathnagar, Hyderabad', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Thermax Environmental STP Technologies', 'STP_PLANT', '+91 40 3344 5566', 'Nageshwar Rao', 'stp.support@thermax.com', 'Phase 2, IDA Cherlapally, Hyderabad', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Tata Power EV Charging Infra Ltd', 'EV_CHARGER', '+91 40 2233 4455', 'Anish Kumar', 'evcharge.hyd@tatapower.com', 'Begumpet Main Road, Hyderabad', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Hikvision Digital Security Systems', 'CCTV_SECURITY', '+91 40 8899 0011', 'Suresh Naidu', 'cctv.hyd@hikvision.in', 'Madhapur Main Road, Hyderabad', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'GreenLeaf Landscaping & Gardens', 'GARDENING', '+91 98490 12345', 'M. Mallesh', 'greenleaf.hyd@gmail.com', 'Gachibowli X Roads, Hyderabad', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 3. ASSETS (Lifts, STP, EV Chargers, CCTV Network, Gensets, Pumps)
INSERT INTO asset (id, name, category, amc_vendor_id, serial_number, purchase_date, purchase_cost, warranty_expiry_date, amc_start_date, amc_expiry_date, amc_cost, amc_details, amc_document_path, status, created_at, updated_at) VALUES
(1, 'Block A Passenger Elevator 01 (16 Passenger)', 'ELEVATOR', 1, 'SCH-BLK-A-P01', '2024-01-15', 3200000.0, '2026-01-14', '2026-01-15', '2027-01-14', 120000.0, 'Full Comprehensive Maintenance with 24x7 Emergency Breakdown Support', 'uploads/members/assets/amc_contracts/Schindler_Elevator_AMC_2026.pdf', 'OPERATIONAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Block A Passenger Elevator 02 (16 Passenger)', 'ELEVATOR', 1, 'SCH-BLK-A-P02', '2024-01-15', 3200000.0, '2026-01-14', '2026-01-15', '2027-01-14', 120000.0, 'Full Comprehensive Maintenance with 24x7 Emergency Support', 'uploads/members/assets/amc_contracts/Schindler_Elevator_AMC_2026.pdf', 'OPERATIONAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Block A Service Elevator (20 Passenger / Stretchers)', 'ELEVATOR', 1, 'SCH-BLK-A-S01', '2024-01-15', 3800000.0, '2026-01-14', '2026-01-15', '2027-01-14', 140000.0, 'Service & Stretcher Lift Comprehensive AMC', 'uploads/members/assets/amc_contracts/Schindler_Elevator_AMC_2026.pdf', 'OPERATIONAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Block B Passenger Elevator 01 (16 Passenger)', 'ELEVATOR', 1, 'SCH-BLK-B-P01', '2024-01-20', 3200000.0, '2026-01-19', '2026-01-20', '2027-01-19', 120000.0, 'Comprehensive Elevator AMC', 'uploads/members/assets/amc_contracts/Schindler_Elevator_AMC_2026.pdf', 'OPERATIONAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Block B Passenger Elevator 02 (16 Passenger)', 'ELEVATOR', 1, 'SCH-BLK-B-P02', '2024-01-20', 3200000.0, '2026-01-19', '2026-01-20', '2027-01-19', 120000.0, 'Comprehensive Elevator AMC', 'uploads/members/assets/amc_contracts/Schindler_Elevator_AMC_2026.pdf', 'OPERATIONAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Block B Service Elevator (20 Passenger)', 'ELEVATOR', 1, 'SCH-BLK-B-S01', '2024-01-20', 3800000.0, '2026-01-19', '2026-01-20', '2027-01-19', 140000.0, 'Service Lift Comprehensive AMC', 'uploads/members/assets/amc_contracts/Schindler_Elevator_AMC_2026.pdf', 'OPERATIONAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'Clubhouse Passenger Elevator (8 Passenger)', 'ELEVATOR', 1, 'SCH-CLUB-P01', '2024-02-10', 1800000.0, '2026-02-09', '2026-02-10', '2027-02-09', 75000.0, 'Clubhouse Elevator AMC', 'uploads/members/assets/amc_contracts/Schindler_Elevator_AMC_2026.pdf', 'OPERATIONAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, '500 KVA Cummins Silent Diesel Generator Set (Main Backup)', 'GENERATOR', 2, 'CUM-500KVA-DG01', '2024-01-10', 4500000.0, '2026-01-09', '2026-01-10', '2027-01-09', 180000.0, 'Quarterly servicing, oil filter replacement, and 24x7 emergency backup response', 'uploads/members/assets/amc_contracts/Cummins_DG_AMC_2026.pdf', 'OPERATIONAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, '250 KVA Sewage & Water Pump Backup Generator', 'GENERATOR', 2, 'CUM-250KVA-DG02', '2024-01-10', 2800000.0, '2026-01-09', '2026-01-10', '2027-01-09', 110000.0, 'Essential Services Genset AMC', 'uploads/members/assets/amc_contracts/Cummins_DG_AMC_2026.pdf', 'OPERATIONAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, '250 KLD Sewage Treatment Plant (STP) & MBR Filtration System', 'STP_PLANT', 3, 'THX-STP-250KLD', '2024-02-01', 6500000.0, '2026-01-31', '2026-02-01', '2027-01-31', 250000.0, 'Monthly bacterial culture dosing, MBR membrane backwash & monthly water quality audit', 'uploads/members/assets/amc_contracts/Thermax_STP_AMC_2026.pdf', 'OPERATIONAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, 'Tata Power 4-Port EV Commercial Fast Charger Bay (Basement 1)', 'EV_CHARGER', 4, 'TATA-EV-B1-01', '2024-03-01', 1200000.0, '2027-02-28', '2026-03-01', '2027-02-28', 60000.0, 'Bi-annual calibration, RFID billing sync & gun replacements', 'uploads/members/assets/amc_contracts/Tata_Power_EV_AMC_2026.pdf', 'OPERATIONAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(12, '96-Camera 4K IP CCTV Surveillance & NVR Server Network', 'CCTV_SECURITY', 5, 'HIK-96CAM-NVR01', '2024-01-25', 1850000.0, '2027-01-24', '2026-01-25', '2027-01-24', 90000.0, '30-day hard drive storage check, lens cleaning, PTZ camera calibration', 'uploads/members/assets/amc_contracts/Hikvision_CCTV_AMC_2026.pdf', 'OPERATIONAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 4. SERVICE LOGS
INSERT INTO asset_service_log (id, asset_id, service_type, service_date, engineer_name, work_summary, cost, document_path, created_by, updated_by, created_at, updated_at) VALUES
(1, 1, 'PREVENTIVE_MAINTENANCE', '2026-07-15', 'Rajesh Varma', 'Quarterly elevator door alignment, brake shoe inspection, and lubrication of guide rails.', 0.0, 'uploads/members/assets/amc_contracts/Schindler_Elevator_AMC_2026.pdf', 'ADMINISTRATOR', 'ADMINISTRATOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 8, 'OIL_CHANGE_AND_FILTERS', '2026-07-20', 'Venkatesh Rao', 'Replaced engine oil (120L), fuel filters, and tested automatic transfer switch (ATS).', 24500.0, 'uploads/members/assets/amc_contracts/Cummins_DG_AMC_2026.pdf', 'ADMINISTRATOR', 'ADMINISTRATOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 10, 'STP_MEMBRANE_CLEANING', '2026-08-01', 'Nageshwar Rao', 'STP MBR Membrane chemical cleaning, blower motor belt tightening, and treated water lab test report.', 15000.0, 'uploads/members/assets/amc_contracts/Thermax_STP_AMC_2026.pdf', 'ADMINISTRATOR', 'ADMINISTRATOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 5. FACILITIES
INSERT INTO facility (id, name, type, location, capacity, timings, booking_price, status, created_at, updated_at) VALUES
(1, 'Grand Central Clubhouse & AC Banquet Hall', 'CLUBHOUSE', 'Clubhouse Building - 1st Floor', 250, '06:00 AM - 10:00 PM', 12000.0, 'OPERATIONAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Mini AC Celebration Hall', 'CLUBHOUSE', 'Clubhouse Building - Ground Floor', 75, '08:00 AM - 10:00 PM', 5000.0, 'OPERATIONAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Half-Olympic Mini Swimming Pool & Toddler Splash Pool', 'SWIMMING_POOL', 'Central Amenities Zone', 40, '06:00 AM - 09:00 PM', 0.0, 'OPERATIONAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Air-Conditioned Guest Suites (Room 101 to 104)', 'GUEST_ROOM', 'Clubhouse - 2nd Floor', 4, '24 Hours Check-in', 2200.0, 'OPERATIONAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Sree Sai Supermarket & Daily Needs Store', 'SUPERMARKET', 'Block A Commercial Arcade', 50, '07:00 AM - 10:00 PM', 0.0, 'OPERATIONAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Activity Room 1 - Indoor Table Tennis & Billiards Arena', 'ACTIVITY_ROOM', 'Clubhouse Basement', 30, '06:00 AM - 09:30 PM', 0.0, 'OPERATIONAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'Activity Room 2 - Chess, Carrom & Card Room', 'ACTIVITY_ROOM', 'Clubhouse Basement', 20, '06:00 AM - 09:30 PM', 0.0, 'OPERATIONAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 'Activity Room 3 - Air-Conditioned Badminton Court', 'ACTIVITY_ROOM', 'Clubhouse 2nd Floor', 20, '06:00 AM - 09:30 PM', 0.0, 'OPERATIONAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 'Activity Room 4 - Yoga & Dance Studio', 'ACTIVITY_ROOM', 'Clubhouse 2nd Floor', 35, '05:30 AM - 09:00 PM', 0.0, 'OPERATIONAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 'Central Landscape Park & Walking Track', 'PARK', 'Central Courtyard', 200, '05:00 AM - 10:00 PM', 0.0, 'OPERATIONAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, 'Children Play Park & Lawn', 'PARK', 'Block B Rear Lawns', 60, '05:30 AM - 08:30 PM', 0.0, 'OPERATIONAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(12, 'Floodlit Sand Volleyball Court', 'VOLLEYBALL_COURT', 'Sports Complex', 24, '06:00 AM - 09:00 PM', 0.0, 'OPERATIONAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 6. SOCIETY STAFF (8 Security Guards + 11 Housekeeping)
INSERT INTO society_staff (id, name, role, mobile_number, shift_timing, police_verification_status, police_doc_path, id_proof_number, active, created_at, updated_at) VALUES
(1, 'M. Kanakaiah', 'SECURITY', '+91 98480 11223', 'Day Shift (06:00 - 18:00)', 'VERIFIED', 'uploads/members/staff/Police_Verification_Kanakaiah.pdf', '3847-2918-4726', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'B. Srinivas', 'SECURITY', '+91 98480 22334', 'Day Shift (06:00 - 18:00)', 'VERIFIED', 'uploads/members/staff/Police_Verification_Srinivas.pdf', '4837-1029-5738', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'K. Yadagiri', 'SECURITY', '+91 98480 33445', 'Day Shift (06:00 - 18:00)', 'VERIFIED', 'uploads/members/staff/Police_Verification_Yadagiri.pdf', '5938-2938-1029', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Ch. Lingaiah', 'SECURITY', '+91 98480 44556', 'Day Shift (06:00 - 18:00)', 'VERIFIED', 'uploads/members/staff/Police_Verification_Lingaiah.pdf', '6948-2019-3847', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'G. Anjaneyulu', 'SECURITY', '+91 98480 55667', 'Night Shift (18:00 - 06:00)', 'VERIFIED', 'uploads/members/staff/Police_Verification_Anjaneyulu.pdf', '7839-2019-4827', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'P. Narsing Rao', 'SECURITY', '+91 98480 66778', 'Night Shift (18:00 - 06:00)', 'VERIFIED', 'uploads/members/staff/Police_Verification_Narsing.pdf', '8937-2910-4827', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'T. Saidulu', 'SECURITY', '+91 98480 77889', 'Night Shift (18:00 - 06:00)', 'VERIFIED', 'uploads/members/staff/Police_Verification_Saidulu.pdf', '9028-1029-3847', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 'V. Bikshapathi', 'SECURITY', '+91 98480 88990', 'Night Shift (18:00 - 06:00)', 'VERIFIED', 'uploads/members/staff/Police_Verification_Bikshapathi.pdf', '1029-3847-5938', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 'Smt. Lakshmiamma', 'HOUSEKEEPING', '+91 97001 11223', 'Morning Shift (07:00 - 15:00)', 'VERIFIED', 'uploads/members/staff/Police_Verification_Lakshmi.pdf', '2938-4728-1029', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 'Smt. Renuka', 'HOUSEKEEPING', '+91 97001 22334', 'Morning Shift (07:00 - 15:00)', 'VERIFIED', 'uploads/members/staff/Police_Verification_Renuka.pdf', '3847-5928-1029', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, 'Smt. Padma', 'HOUSEKEEPING', '+91 97001 33445', 'Morning Shift (07:00 - 15:00)', 'VERIFIED', 'uploads/members/staff/Police_Verification_Padma.pdf', '4837-2918-5928', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(12, 'Smt. Saroja', 'HOUSEKEEPING', '+91 97001 44556', 'Morning Shift (07:00 - 15:00)', 'VERIFIED', 'uploads/members/staff/Police_Verification_Saroja.pdf', '5938-1029-3847', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(13, 'Smt. Andalamma', 'HOUSEKEEPING', '+91 97001 55667', 'Morning Shift (07:00 - 15:00)', 'VERIFIED', 'uploads/members/staff/Police_Verification_Andal.pdf', '6948-3829-1029', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(14, 'Smt. Pushpa', 'HOUSEKEEPING', '+91 97001 66778', 'Morning Shift (07:00 - 15:00)', 'VERIFIED', 'uploads/members/staff/Police_Verification_Pushpa.pdf', '7839-4827-1029', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(15, 'Smt. Venkatamma', 'HOUSEKEEPING', '+91 97001 77889', 'Afternoon Shift (13:00 - 21:00)', 'VERIFIED', 'uploads/members/staff/Police_Verification_Venkatamma.pdf', '8937-5928-1029', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(16, 'Smt. Yellamma', 'HOUSEKEEPING', '+91 97001 88990', 'Afternoon Shift (13:00 - 21:00)', 'VERIFIED', 'uploads/members/staff/Police_Verification_Yellamma.pdf', '9028-4827-1029', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(17, 'Smt. Satyavathi', 'HOUSEKEEPING', '+91 97001 99001', 'Afternoon Shift (13:00 - 21:00)', 'VERIFIED', 'uploads/members/staff/Police_Verification_Satya.pdf', '1029-4827-5928', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(18, 'M. Mahesh', 'HOUSEKEEPING', '+91 97002 00112', 'General Shift (08:00 - 17:00)', 'VERIFIED', 'uploads/members/staff/Police_Verification_Mahesh.pdf', '2938-5928-1029', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(19, 'K. Naresh', 'HOUSEKEEPING', '+91 97002 11223', 'General Shift (08:00 - 17:00)', 'VERIFIED', 'uploads/members/staff/Police_Verification_Naresh.pdf', '3847-4827-1029', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 7. MEMBERS (Committee & Representative Flat Owners)
INSERT INTO member (id, member_number, membership_number, first_name, middle_name, last_name, gender, dob, occupation, mobile_number, email, emergency_contact_name, emergency_contact_phone, member_type, admission_date, resolution_number, resolution_date, permanent_address, correspondence_address, status, active, created_at, updated_at) VALUES
(1, 'MEM-001', 'SST-MEM-001', 'Ramesh', 'Venkata', 'Rao', 'MALE', '1975-06-15', 'IT Vice President', '+91 98490 99887', 'ramesh.rao@gmail.com', 'Smt. Radhika Rao', '+91 98490 99888', 'PRIMARY', '2024-02-01', 'RES-2024-001', '2024-01-20', 'Flat A-1501, Block A, Sree Sai Trust Society, Gachibowli, Hyderabad', 'Flat A-1501, Sree Sai Trust Society, Hyderabad', 'ACTIVE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'MEM-002', 'SST-MEM-002', 'Sunitha', '', 'Reddy', 'FEMALE', '1982-08-22', 'Chartered Accountant', '+91 98491 88776', 'sunitha.reddy@ca.in', 'Sri Vikram Reddy', '+91 98491 88777', 'PRIMARY', '2024-02-01', 'RES-2024-002', '2024-01-20', 'Flat B-1402, Block B, Sree Sai Trust Society, Gachibowli, Hyderabad', 'Flat B-1402, Sree Sai Trust Society, Hyderabad', 'ACTIVE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'MEM-003', 'SST-MEM-003', 'K. V.', '', 'Sharma', 'MALE', '1968-11-10', 'Senior Advocate', '+91 98492 77665', 'sharma.legal@gmail.com', 'Smt. Lakshmi Sharma', '+91 98492 77666', 'PRIMARY', '2024-02-01', 'RES-2024-003', '2024-01-20', 'Villa 05, Sree Sai Trust Society, Gachibowli, Hyderabad', 'Villa 05, Sree Sai Trust Society, Hyderabad', 'ACTIVE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'MEM-004', 'SST-MEM-004', 'Dr. Srinivas', 'Kumar', 'Joshi', 'MALE', '1980-03-30', 'Cardiologist', '+91 98493 66554', 'dr.joshi@apollo.com', 'Smt. Archana Joshi', '+91 98493 66555', 'PRIMARY', '2024-02-15', 'RES-2024-008', '2024-02-10', 'Flat A-1204, Block A, Sree Sai Trust Society, Gachibowli, Hyderabad', 'Flat A-1204, Sree Sai Trust Society, Hyderabad', 'ACTIVE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'MEM-005', 'SST-MEM-005', 'Anusha', '', 'Chowdary', 'FEMALE', '1990-09-18', 'Software Architect', '+91 98494 55443', 'anusha.c@techcorp.com', 'Sri Harsha Chowdary', '+91 98494 55444', 'PRIMARY', '2024-03-01', 'RES-2024-012', '2024-02-25', 'Villa 12, Sree Sai Trust Society, Gachibowli, Hyderabad', 'Villa 12, Sree Sai Trust Society, Hyderabad', 'ACTIVE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 8. PROPERTIES (240 Apartments across 2 Blocks 15 Floors + 15 Villas)
-- Insert 240 Flat Properties + 15 Villa Properties
INSERT INTO property (id, property_number, block, type, current_owner_id, created_at, updated_at) VALUES
(1, 'A-1501', 'Block A', 'APARTMENT', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'B-1402', 'Block B', 'APARTMENT', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'A-1204', 'Block A', 'APARTMENT', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Villa 05', 'Villas Zone', 'VILLA', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Villa 12', 'Villas Zone', 'VILLA', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 9. MANAGING COMMITTEE
INSERT INTO managing_committee (id, member_id, designation, start_date, end_date, status, created_at, updated_at) VALUES
(1, 1, 'PRESIDENT', '2024-02-15', '2027-02-14', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, 'SECRETARY', '2024-02-15', '2027-02-14', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 3, 'TREASURER', '2024-02-15', '2027-02-14', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 10. GOVERNANCE MEETINGS & RESOLUTIONS
INSERT INTO meeting (id, title, meeting_type, meeting_date, meeting_time, venue, status, minutes, created_at, updated_at) VALUES
(1, 'First General Body Meeting & Bylaws Adoption', 'AGM', '2024-02-15', '10:00 AM', 'Grand Central Clubhouse Banquet Hall', 'COMPLETED', 'Adopted society model bylaws, elected Office Bearers, and approved 500 parking allocations for 240 flats and 15 villas.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Managing Committee Meeting - Q3 Maintenance & STP Audit', 'COMMITTEE', '2026-08-25', '06:00 PM', 'Society Board Room', 'SCHEDULED', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO resolution (id, resolution_number, subject, description, meeting_id, status, created_at, updated_at) VALUES
(1, 'RES-2024-001', 'Approval of Society Model Bylaws & Rules', 'Unanimously approved model bylaws under Telangana Co-operative Societies Act for Sree Sai Trust Society.', 1, 'PASSED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'RES-2024-002', 'Allocation of 2 Covered Parking Bays per Flat', 'Resolved to allocate 2 dedicated covered parking slots to each apartment and villa.', 1, 'PASSED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 11. FINANCIAL EXPENSES & BANK ACCOUNTS
INSERT INTO bank_account (id, bank_name, account_number, ifsc, branch, balance, created_at, updated_at) VALUES
(1, 'State Bank of India', '40982736152', 'SBIN0021045', 'Financial District Gachibowli', 0.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'HDFC Bank Ltd', '50100293847', 'HDFC0001245', 'Hitec City Branch', 0.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO expense (id, voucher_number, expense_date, payee, category, amount, payment_mode, bank_account_id, remarks, created_at, updated_at) VALUES
(1, 'VOU-2026-001', '2026-07-15', 'Schindler Elevators India Pvt Ltd', 'MAINTENANCE', 120000.0, 'BANK_TRANSFER', 1, 'Q3 Quarterly Lift AMC Payment for Block A & Block B Lifts', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'VOU-2026-002', '2026-07-20', 'Cummins India Genset Services', 'REPAIRS', 24500.0, 'BANK_TRANSFER', 1, 'Diesel Genset 500KVA B-Check Service & Filter replacement', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'VOU-2026-003', '2026-08-01', 'Thermax Environmental STP Technologies', 'UTILITIES', 15000.0, 'CHEQUE', 2, 'STP 250KLD Plant bacterial dosing & membrane maintenance', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 12. STATUTORY DOCUMENTS
INSERT INTO document (id, title, category, file_path, file_size, created_at, updated_at) VALUES
(1, 'Model Bye-Laws & Rules of Sree Sai Trust Society', 'BYELAWS', 'uploads/members/byelaws/Sree_Sai_Trust_Society_ByeLaws.pdf', 1024, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Schindler Elevator AMC Agreement 2026', 'AGREEMENT', 'uploads/members/assets/amc_contracts/Schindler_Elevator_AMC_2026.pdf', 2048, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Thermax 250KLD STP Maintenance Agreement', 'AGREEMENT', 'uploads/members/assets/amc_contracts/Thermax_STP_AMC_2026.pdf', 2048, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
