package com.dnd.runus.infrastructure.persistence.domain.scale;

import com.dnd.runus.domain.common.Coordinate;
import com.dnd.runus.domain.common.Pace;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.domain.scale.Scale;
import com.dnd.runus.domain.scale.ScaleAchievement;
import com.dnd.runus.domain.scale.ScaleAchievementRepository;
import com.dnd.runus.domain.scale.ScaleRepository;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.global.constant.RunningEmoji;
import com.dnd.runus.infrastructure.persistence.annotation.RepositoryTest;
import com.dnd.runus.infrastructure.persistence.jpa.scale.entity.ScaleEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RepositoryTest
public class ScaleRepositoryImplTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private ScaleRepository scaleRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RunningRecordRepository runningRecordRepository;

    @Autowired
    private ScaleAchievementRepository scaleAchievementRepository;

    private Long scale1Id;
    private Long scale2Id;
    private Long scale3Id;

    @BeforeEach
    void setUp() {
        // insert test data
        em.persist(ScaleEntity.from(new Scale(0, "scale1", 1_000_000, 1, "서울(한국)", "도쿄(일본)"))); // 누적 달성 거리 : 1_000_000
        em.persist(ScaleEntity.from(new Scale(0, "scale2", 2_100_000, 2, "도쿄(일본)", "베이징(중국)"))); // 누적 달성 거리 : 3_100_000
        em.persist(
                ScaleEntity.from(new Scale(0, "scale3", 1_000_000, 3, "베이징(중국)", "타이베이(대만)"))); // 누적 달성 거리 : 4_100_000
        em.persist(
                ScaleEntity.from(new Scale(0, "scale4", 1_000_000, 4, "베이징(중국)", "타이베이(대만)"))); // 누적 달성 거리 : 5_100_000
        em.flush();

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ScaleEntity> query = criteriaBuilder.createQuery(ScaleEntity.class);
        Root<ScaleEntity> from = query.from(ScaleEntity.class);
        query.select(from).orderBy(criteriaBuilder.asc(from.get("index")));

        List<Long> idList =
                em.createQuery(query).getResultStream().map(ScaleEntity::getId).toList();

        scale1Id = idList.get(0);
        scale2Id = idList.get(1);
        scale3Id = idList.get(2);
    }

    @DisplayName("scale_achievement에 기록이 없는 경우 성취 가능한 scale_id를 반환한다.")
    @Test
    void findAchievableScaleIdsWithoutNoAchievementRecords() {
        // given
        Member savedMember = memberRepository.save(
                new Member(1L, MemberRole.USER, "nickname", OffsetDateTime.now(), OffsetDateTime.now()));
        for (int i = 0; i < 3; i++) {
            RunningRecord runningRecord = new RunningRecord(
                    0,
                    savedMember,
                    1_100_000,
                    Duration.ofHours(12).plusMinutes(23).plusSeconds(56),
                    1,
                    new Pace(5, 11),
                    OffsetDateTime.now(),
                    OffsetDateTime.now().plusHours(1),
                    List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                    "start location",
                    "end location",
                    RunningEmoji.SOSO);
            runningRecordRepository.save(runningRecord);
        }

        // when
        List<Long> achievableScaleIds = scaleRepository.findAchievableScaleIds(savedMember.memberId());

        // then
        assertNotNull(achievableScaleIds);
        assertThat(achievableScaleIds.size()).isEqualTo(2);
        assertTrue(achievableScaleIds.contains(scale1Id));
        assertTrue(achievableScaleIds.contains(scale2Id));
    }

    @DisplayName("scale_achievement에 기록이 존재 할 경우, 성취 가능한 scale_id를 반환한다.")
    @Test
    void findAchievableScaleIds() {
        // given
        Member savedMember = memberRepository.save(
                new Member(1L, MemberRole.USER, "nickname", OffsetDateTime.now(), OffsetDateTime.now()));
        runningRecordRepository.save(new RunningRecord(
                0,
                savedMember,
                1_100_000,
                Duration.ofHours(12).plusMinutes(23).plusSeconds(56),
                1,
                new Pace(5, 11),
                OffsetDateTime.now(),
                OffsetDateTime.now().plusHours(1),
                List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                "start location",
                "end location",
                RunningEmoji.SOSO));
        // scale1 기록 달성(달성 거리 1_000_000 달성), 현재 누적 거리 : 1_100_000
        scaleAchievementRepository.saveAll(
                List.of(new ScaleAchievement(savedMember, new Scale(scale1Id), OffsetDateTime.now())));

        for (int i = 0; i < 3; i++) {
            RunningRecord runningRecord = new RunningRecord(
                    0,
                    savedMember,
                    1_100_000,
                    Duration.ofHours(12).plusMinutes(23).plusSeconds(56),
                    1,
                    new Pace(5, 11),
                    OffsetDateTime.now(),
                    OffsetDateTime.now().plusHours(1),
                    List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                    "start location",
                    "end location",
                    RunningEmoji.SOSO);
            runningRecordRepository.save(runningRecord);
        } // 현재 누넉 거리 : 4_400_000

        // when
        List<Long> achievableScaleIds = scaleRepository.findAchievableScaleIds(savedMember.memberId());

        // then
        assertNotNull(achievableScaleIds);
        assertThat(achievableScaleIds.size()).isEqualTo(2);
        assertTrue(achievableScaleIds.contains(scale2Id));
        assertTrue(achievableScaleIds.contains(scale3Id));
    }
}
