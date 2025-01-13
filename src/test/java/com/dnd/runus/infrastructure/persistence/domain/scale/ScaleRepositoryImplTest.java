package com.dnd.runus.infrastructure.persistence.domain.scale;

import com.dnd.runus.domain.common.CoordinatePoint;
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
import java.time.ZonedDateTime;
import java.util.Comparator;
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

    private Scale scale1;
    private Scale scale2;
    private Scale scale3;
    private Scale scale4;

    @BeforeEach
    void setUp() {
        // insert test data
        em.persist(ScaleEntity.from(new Scale(0, "scale1", 1_000_000, 1, "서울(한국)", "도쿄(일본)"))); // 누적 달성 거리 : 1_000_000
        em.persist(ScaleEntity.from(new Scale(0, "scale2", 2_100_000, 2, "도쿄(일본)", "베이징(중국)"))); // 누적 달성 거리 : 3_100_000
        em.persist(
                ScaleEntity.from(new Scale(0, "scale3", 1_000_000, 4, "베이징(중국)", "타이베이(대만)"))); // 누적 달성 거리 : 4_100_000
        em.persist(
                ScaleEntity.from(new Scale(0, "scale4", 1_000_000, 3, "베이징(중국)", "타이베이(대만)"))); // 누적 달성 거리 : 5_100_000
        em.flush();

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ScaleEntity> query = criteriaBuilder.createQuery(ScaleEntity.class);
        Root<ScaleEntity> from = query.from(ScaleEntity.class);
        query.select(from).orderBy(criteriaBuilder.asc(from.get("index")));

        List<Scale> scales = em.createQuery(query)
                .getResultStream()
                .sorted(Comparator.comparing(ScaleEntity::getIndex))
                .map(ScaleEntity::toDomain)
                .toList();

        scale1 = scales.get(0); // total sum : 1_000_000
        scale2 = scales.get(1); // total sum : 3_100_000
        scale4 = scales.get(2); // total sum : 4_100_000
        scale3 = scales.get(3); // total sum : 5_100_000
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
                    ZonedDateTime.now(),
                    ZonedDateTime.now().plusHours(1),
                    List.of(new CoordinatePoint(1, 2, 3), new CoordinatePoint(4, 5, 6)),
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
        assertTrue(achievableScaleIds.contains(scale1.scaleId()));
        assertTrue(achievableScaleIds.contains(scale2.scaleId()));
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
                ZonedDateTime.now(),
                ZonedDateTime.now().plusHours(1),
                List.of(new CoordinatePoint(1, 2, 3), new CoordinatePoint(4, 5, 6)),
                "start location",
                "end location",
                RunningEmoji.SOSO));
        // scale1 기록 달성(달성 거리 1_000_000 달성), 현재 누적 거리 : 1_100_000
        scaleAchievementRepository.saveAll(
                List.of(new ScaleAchievement(savedMember, new Scale(scale1.scaleId()), OffsetDateTime.now())));

        for (int i = 0; i < 2; i++) {
            RunningRecord runningRecord = new RunningRecord(
                    0,
                    savedMember,
                    1_100_000,
                    Duration.ofHours(12).plusMinutes(23).plusSeconds(56),
                    1,
                    new Pace(5, 11),
                    ZonedDateTime.now(),
                    ZonedDateTime.now().plusHours(1),
                    List.of(new CoordinatePoint(1, 2, 3), new CoordinatePoint(4, 5, 6)),
                    "start location",
                    "end location",
                    RunningEmoji.SOSO);
            runningRecordRepository.save(runningRecord);
        } // 현재 누넉 거리 : 3_300_000

        // when
        List<Long> achievableScaleIds = scaleRepository.findAchievableScaleIds(savedMember.memberId());

        // then
        assertNotNull(achievableScaleIds);
        assertThat(achievableScaleIds.size()).isEqualTo(1);
        assertTrue(achievableScaleIds.contains(scale2.scaleId()));
    }

    @DisplayName("scaled의 id와 index값의 순서가 달를 경우, 성취 가능한 scale_id를 올바르게 반환한다.")
    @Test
    void findAchievableScaleIds_diffIDAndIndex() {
        // given
        Member savedMember = memberRepository.save(
                new Member(1L, MemberRole.USER, "nickname", OffsetDateTime.now(), OffsetDateTime.now()));

        for (int i = 0; i < 4; i++) {
            RunningRecord runningRecord = new RunningRecord(
                    0,
                    savedMember,
                    1_100_000,
                    Duration.ofHours(12).plusMinutes(23).plusSeconds(56),
                    1,
                    new Pace(5, 11),
                    ZonedDateTime.now(),
                    ZonedDateTime.now().plusHours(1),
                    List.of(new CoordinatePoint(1, 2, 3), new CoordinatePoint(4, 5, 6)),
                    "start location",
                    "end location",
                    RunningEmoji.SOSO);
            runningRecordRepository.save(runningRecord);
        } // 현재 누넉 거리 : 4_400_000

        // when
        List<Long> achievableScaleIds = scaleRepository.findAchievableScaleIds(savedMember.memberId());

        // then
        assertNotNull(achievableScaleIds);
        assertThat(achievableScaleIds.size()).isEqualTo(3);
        assertTrue(achievableScaleIds.contains(scale1.scaleId()));
        assertTrue(achievableScaleIds.contains(scale2.scaleId()));
        assertTrue(achievableScaleIds.contains(scale4.scaleId()));
    }
}
