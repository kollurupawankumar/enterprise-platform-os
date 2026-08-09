CREATE TABLE meeting_attendance (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    meeting_id INTEGER NOT NULL,
    member_id INTEGER NOT NULL,
    status TEXT NOT NULL DEFAULT 'ABSENT', -- PRESENT, ABSENT, APOLOGY
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (meeting_id) REFERENCES meeting(id),
    FOREIGN KEY (member_id) REFERENCES member(id),
    CONSTRAINT unique_meeting_member UNIQUE (meeting_id, member_id)
);
