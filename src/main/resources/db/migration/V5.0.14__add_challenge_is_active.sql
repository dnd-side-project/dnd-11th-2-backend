ALTER TABLE challenge
    ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE;

UPDATE challenge
SET is_active = TRUE;
