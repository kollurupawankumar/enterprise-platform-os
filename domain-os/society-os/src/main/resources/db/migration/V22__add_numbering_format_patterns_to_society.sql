ALTER TABLE society ADD COLUMN member_number_format TEXT DEFAULT 'MEM-{SEQ}';
ALTER TABLE society ADD COLUMN share_certificate_format TEXT DEFAULT 'SC-{SEQ}';
