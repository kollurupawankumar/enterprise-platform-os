CREATE TABLE application_metadata
(
    id                  INTEGER PRIMARY KEY,

    schema_version      TEXT NOT NULL,

    application_version TEXT NOT NULL,

    created_at          TEXT NOT NULL
);

INSERT INTO application_metadata
(
    id,
    schema_version,
    application_version,
    created_at
)
VALUES
    (
        1,
        '1',
        '1.0.0',
        CURRENT_TIMESTAMP
    );