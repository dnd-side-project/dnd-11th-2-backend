package com.dnd.runus.infrastructure.persistence.domain.scale;

import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.scale.Scale;
import com.dnd.runus.domain.scale.ScaleAchievement;
import com.dnd.runus.domain.scale.ScaleAchievementLog;
import com.dnd.runus.domain.scale.ScaleAchievementRepository;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.infrastructure.persistence.annotation.RepositoryTest;
import com.dnd.runus.infrastructure.persistence.jpa.member.entity.MemberEntity;
import com.dnd.runus.infrastructure.persistence.jpa.scale.entity.ScaleEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RepositoryTest
public class ScaleAchievementRepositoryTest {

    @Autowired
    private ScaleAchievementRepository scaleAchievementRepository;

    @Autowired
    private EntityManager em;

    private Member member;
    private Scale scale1;
    private Scale scale2;

    @BeforeEach
    void setUp() {
        MemberEntity memberEntity = MemberEntity.from(new Member(MemberRole.USER, "nickname"));
        em.persist(memberEntity);
        ScaleEntity scaleEntity1 = ScaleEntity.from(new Scale(0, "test", 200, 1, "test1", "test2"));
        ScaleEntity scaleEntity2 = ScaleEntity.from(new Scale(0, "test11", 1000, 2, "test1", "test2"));
        em.persist(scaleEntity1);
        em.persist(scaleEntity2);
        em.flush();
        member = memberEntity.toDomain();
        scale1 = scaleEntity1.toDomain();
        scale2 = scaleEntity2.toDomain();
    }

    @Test
    @DisplayName("해당 코스를 달성했다면, achievedDate는 null이 아니다.")
    void findScaleAchievementLogs() {
        scaleAchievementRepository.saveAll(List.of(new ScaleAchievement(member, scale1, OffsetDateTime.now())));
        List<ScaleAchievementLog> scaleAchievementLogs =
                scaleAchievementRepository.findScaleAchievementLogs(member.memberId());

        assertEquals(2, scaleAchievementLogs.size());

        assertEquals(scale1, scaleAchievementLogs.get(0).scale());
        assertNotNull(scaleAchievementLogs.get(0).achievedDate());

        assertEquals(scale2, scaleAchievementLogs.get(1).scale());
        assertNull(scaleAchievementLogs.get(1).achievedDate());
    }

    @Test
    @DisplayName("코스 성취 기록을 저장한다.")
    void saveTest() {
        List<ScaleAchievement> saved = scaleAchievementRepository.saveAll(List.of(
                new ScaleAchievement(member, new Scale(scale1.scaleId()), OffsetDateTime.now()),
                new ScaleAchievement(member, new Scale(scale2.scaleId()), OffsetDateTime.now())));

        assertNotNull(saved);
        assertNotNull(saved.get(0));
    }
}
