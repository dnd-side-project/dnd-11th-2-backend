ALTER TABLE running_record
    ALTER COLUMN average_pace TYPE INTEGER USING average_pace::INTEGER;
