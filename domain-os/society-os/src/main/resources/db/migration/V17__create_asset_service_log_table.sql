CREATE TABLE asset_service_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    asset_id INTEGER NOT NULL,
    service_date TEXT NOT NULL,
    engineer_name TEXT,
    service_type TEXT NOT NULL DEFAULT 'ROUTINE_AMC', -- ROUTINE_AMC, REPAIR, INSPECTION, EMERGENCY
    work_summary TEXT,
    cost REAL DEFAULT 0.0,
    document_path TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (asset_id) REFERENCES asset(id)
);
