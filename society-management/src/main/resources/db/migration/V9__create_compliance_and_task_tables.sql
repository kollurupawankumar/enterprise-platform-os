CREATE TABLE compliance (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    category TEXT NOT NULL,
    description TEXT,
    due_date TEXT NOT NULL,
    reminder_date TEXT,
    status TEXT NOT NULL DEFAULT 'PENDING',
    completed_date TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE task (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    description TEXT,
    due_date TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
