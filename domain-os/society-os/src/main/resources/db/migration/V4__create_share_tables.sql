CREATE TABLE share_certificate (
    id INTEGER PRIMARY KEY AUTOINCREMENT,

    certificate_number TEXT NOT NULL UNIQUE,

    member_id INTEGER NOT NULL,

    from_share_number INTEGER NOT NULL,

    to_share_number INTEGER NOT NULL,

    total_shares INTEGER NOT NULL,

    face_value_per_share REAL NOT NULL DEFAULT 50.0,

    total_amount REAL NOT NULL,

    issue_date TEXT NOT NULL,

    status TEXT NOT NULL DEFAULT 'ACTIVE',

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    FOREIGN KEY (member_id) REFERENCES member(id)
);

CREATE TABLE share_transfer_history (
    id INTEGER PRIMARY KEY AUTOINCREMENT,

    certificate_id INTEGER NOT NULL,

    from_member_id INTEGER NOT NULL,

    to_member_id INTEGER NOT NULL,

    transfer_date TEXT NOT NULL,

    transfer_fee REAL DEFAULT 0.0,

    remarks TEXT,

    created_at TIMESTAMP NOT NULL,

    FOREIGN KEY (certificate_id) REFERENCES share_certificate(id),
    FOREIGN KEY (from_member_id) REFERENCES member(id),
    FOREIGN KEY (to_member_id) REFERENCES member(id)
);
