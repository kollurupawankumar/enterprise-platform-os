CREATE TABLE member (

                        id INTEGER PRIMARY KEY AUTOINCREMENT,

                        member_number TEXT NOT NULL UNIQUE,

                        membership_number TEXT UNIQUE,

                        first_name TEXT NOT NULL,

                        last_name TEXT,

                        mobile_number TEXT NOT NULL,

                        email TEXT,

                        aadhaar_number TEXT,

                        pan_number TEXT,

                        status TEXT NOT NULL,

                        active INTEGER NOT NULL DEFAULT 1,

                        created_at TIMESTAMP NOT NULL,
                        updated_at TIMESTAMP NOT NULL

);