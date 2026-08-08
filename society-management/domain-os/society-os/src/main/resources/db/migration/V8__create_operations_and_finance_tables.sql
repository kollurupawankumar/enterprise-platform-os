CREATE TABLE vendor (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    category TEXT NOT NULL,
    contact_person TEXT,
    phone TEXT,
    email TEXT,
    address TEXT,
    active INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE asset (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    category TEXT NOT NULL,
    serial_number TEXT,
    purchase_date TEXT,
    purchase_cost REAL,
    warranty_expiry_date TEXT,
    status TEXT NOT NULL DEFAULT 'OPERATIONAL',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE project (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    vendor_id INTEGER,
    budget REAL,
    start_date TEXT,
    end_date TEXT,
    status TEXT NOT NULL DEFAULT 'PROPOSED',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (vendor_id) REFERENCES vendor(id)
);

CREATE TABLE bank_account (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    bank_name TEXT NOT NULL,
    account_number TEXT NOT NULL UNIQUE,
    ifsc TEXT NOT NULL,
    branch TEXT,
    balance REAL NOT NULL DEFAULT 0.0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE expense (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    voucher_number TEXT NOT NULL UNIQUE,
    expense_date TEXT NOT NULL,
    payee TEXT NOT NULL,
    category TEXT NOT NULL,
    amount REAL NOT NULL,
    payment_mode TEXT NOT NULL,
    bank_account_id INTEGER,
    remarks TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (bank_account_id) REFERENCES bank_account(id)
);

CREATE TABLE investment (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    investment_type TEXT NOT NULL,
    institution TEXT NOT NULL,
    reference_number TEXT NOT NULL UNIQUE,
    principal_amount REAL NOT NULL,
    interest_rate REAL,
    start_date TEXT NOT NULL,
    maturity_date TEXT,
    status TEXT NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
