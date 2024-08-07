-- 기존 member_level 테이블의 잘못된 FK constraint (member_id) 제거
ALTER TABLE member_level DROP CONSTRAINT fk_member_level_level;
-- 올바른 FK constraint 추가
ALTER TABLE member_level ADD CONSTRAINT fk_member FOREIGN KEY (member_id) REFERENCES member (id);
