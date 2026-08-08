CREATE TABLE facility (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    type TEXT NOT NULL,
    location TEXT,
    capacity INTEGER,
    timings TEXT,
    status TEXT NOT NULL DEFAULT 'OPERATIONAL',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE society_staff (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    role TEXT NOT NULL,
    mobile_number TEXT NOT NULL,
    id_proof_number TEXT,
    shift_timing TEXT,
    police_verification_status TEXT NOT NULL DEFAULT 'PENDING',
    police_doc_path TEXT,
    active INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
