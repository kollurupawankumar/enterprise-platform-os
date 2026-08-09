CREATE TABLE meeting (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    meeting_type TEXT NOT NULL,
    meeting_date TEXT NOT NULL,
    meeting_time TEXT,
    venue TEXT NOT NULL,
    agenda TEXT,
    minutes TEXT,
    status TEXT NOT NULL DEFAULT 'SCHEDULED',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE resolution (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    resolution_number TEXT NOT NULL UNIQUE,
    subject TEXT NOT NULL,
    description TEXT,
    meeting_id INTEGER NOT NULL,
    proposed_by TEXT,
    seconded_by TEXT,
    status TEXT NOT NULL DEFAULT 'PASSED',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (meeting_id) REFERENCES meeting(id)
);

CREATE TABLE document (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    category TEXT NOT NULL,
    tags TEXT,
    file_path TEXT NOT NULL,
    file_size INTEGER NOT NULL,
    mime_type TEXT,
    expiry_date TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
