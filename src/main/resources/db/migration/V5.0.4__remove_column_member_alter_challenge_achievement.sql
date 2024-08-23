ALTER TABLE challenge_achievement
DROP CONSTRAINT fk_challenge_achievement_member,
    DROP COLUMN member_id;
