CREATE TABLE society
(
    id INTEGER PRIMARY KEY AUTOINCREMENT,

    name TEXT NOT NULL,

    short_name TEXT NOT NULL,

    registration_number TEXT NOT NULL,

    address_line1 TEXT,

    address_line2 TEXT,

    city TEXT,

    state TEXT,

    pin_code TEXT,

    phone TEXT,

    email TEXT,

    website TEXT,

    financial_year_start_month TEXT NOT NULL,

    logo_path TEXT,

    seal_path TEXT,

    active INTEGER NOT NULL DEFAULT 1
);