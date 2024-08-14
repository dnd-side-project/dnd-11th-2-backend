ALTER TABLE member DROP COLUMN main_badge_id;
ALTER TABLE badge RENAME COLUMN image_path TO image_url;
ALTER TABLE badge ALTER COLUMN image_url TYPE VARCHAR(500);
