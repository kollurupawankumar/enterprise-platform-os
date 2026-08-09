CREATE TABLE knowledge_base (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    category TEXT NOT NULL,
    content TEXT NOT NULL,
    author TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE budget (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    year TEXT NOT NULL,
    category TEXT NOT NULL,
    allocated_amount REAL NOT NULL,
    actual_spent REAL NOT NULL DEFAULT 0.0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE audit_observation (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    observation_date TEXT NOT NULL,
    auditor_name TEXT NOT NULL,
    observation_text TEXT NOT NULL,
    query_tracker TEXT,
    status TEXT NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
