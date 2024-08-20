ALTER TABLE running_record ADD COLUMN end_location VARCHAR(50);
ALTER TABLE running_record RENAME COLUMN location TO start_location;
ALTER TABLE running_record ALTER COLUMN start_location TYPE VARCHAR(50);
