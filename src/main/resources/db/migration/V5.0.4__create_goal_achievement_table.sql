CREATE TABLE goal_achievement
(
    id                  bigint  GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    member_id           bigint  NOT NULL
        CONSTRAINT fk_goal_achievement_member REFERENCES member,
    running_record_id          bigint  NOT NULL
        CONSTRAINT uk_goal_achievement_running_id UNIQUE
        CONSTRAINT fk_goal_achievement_running_id REFERENCES running_record,
    goal_type           varchar(25) NOT NULL CHECK (goal_type IN ('TIME', 'DISTANCE')),
    achievement_value        integer NOT NULL,
    is_achieved     bool NOT NULL,
    created_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone
);

-- add comment
COMMENT ON COLUMN goal_achievement.member_id IS '사용자 id';
COMMENT ON COLUMN goal_achievement.running_record_id IS '러닝 기록 id';
COMMENT ON COLUMN goal_achievement.goal_type IS '목표 타입 enum(TIME, DISTANCE)';
COMMENT ON COLUMN goal_achievement.achievement_value IS '목표 값';
COMMENT ON COLUMN goal_achievement.is_achieved IS '목표 성취 여부';
