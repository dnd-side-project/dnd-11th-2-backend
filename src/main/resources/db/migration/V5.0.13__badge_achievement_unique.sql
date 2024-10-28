ALTER TABLE badge_achievement
    ADD CONSTRAINT unique_badge_member UNIQUE (badge_id, member_id);
