ALTER TABLE post
ADD COLUMN verified BOOLEAN DEFAULT FALSE;
ALTER TABLE post
ADD COLUMN verified_at TIMESTAMP;