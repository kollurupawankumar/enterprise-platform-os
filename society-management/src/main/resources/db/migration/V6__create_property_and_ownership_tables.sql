CREATE TABLE property (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    property_number TEXT NOT NULL UNIQUE,
    block TEXT NOT NULL,
    type TEXT NOT NULL,
    current_owner_id INTEGER,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (current_owner_id) REFERENCES member(id)
);

CREATE TABLE ownership_history (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    property_id INTEGER NOT NULL,
    member_id INTEGER NOT NULL,
    from_date TEXT NOT NULL,
    to_date TEXT,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (property_id) REFERENCES property(id),
    FOREIGN KEY (member_id) REFERENCES member(id)
);

CREATE TABLE joint_owner (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    member_id INTEGER NOT NULL,
    first_name TEXT NOT NULL,
    last_name TEXT,
    relationship TEXT NOT NULL,
    aadhaar_number TEXT,
    pan_number TEXT,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (member_id) REFERENCES member(id)
);

CREATE TABLE nominee (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    member_id INTEGER NOT NULL,
    first_name TEXT NOT NULL,
    last_name TEXT,
    relationship TEXT NOT NULL,
    share_percentage REAL NOT NULL DEFAULT 100.0,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (member_id) REFERENCES member(id)
);
