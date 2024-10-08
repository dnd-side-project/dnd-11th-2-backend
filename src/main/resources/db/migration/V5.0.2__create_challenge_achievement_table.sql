CREATE TABLE challenge_achievement
(
    id                  bigint  GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    member_id           bigint  NOT NULL
        CONSTRAINT fk_challenge_achievement_member REFERENCES member,
    running_record_id          bigint  NOT NULL
        CONSTRAINT uk_challenge_achievement_running_id UNIQUE
        CONSTRAINT fk_challenge_achievement_running_id REFERENCES running_record,
    challenge_id        bigint  NOT NULL,
    success_status      bool    NOT NULL,
    has_percentage      bool    NOT NULL,
    start_value         integer NULL,
    end_value           integer NULL,
    achievement_value   integer NULL,
    percentage          integer NULL,
    created_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone
);

-- add comment
COMMENT ON COLUMN challenge_achievement.member_id IS '사용자 id';
COMMENT ON COLUMN challenge_achievement.running_record_id IS '러닝 기록 id';
COMMENT ON COLUMN challenge_achievement.challenge_id IS '챌린지 id';
COMMENT ON COLUMN challenge_achievement.success_status IS '챌린지 성공 여부(성공:true, 실패:false)';
COMMENT ON COLUMN challenge_achievement.has_percentage IS '챌린지 결과 퍼센테이지바 존재 여부{존재:true(거리, 시간 챌린지일 경우), 존재X:false}';
COMMENT ON COLUMN challenge_achievement.start_value IS '퍼센테이지바 존재 시 챌린지 시작 값(거리:0, 시간:러닝 시작 시각 등)';
COMMENT ON COLUMN challenge_achievement.end_value IS '퍼센테이지바 존재 시 챌린지 끝(목표) 값(거리:목표 값, 시간: 러닝 시작 시간 + 목표 값)';
COMMENT ON COLUMN challenge_achievement.percentage IS '퍼센테이지바 존재 시 퍼센테이지 값';
