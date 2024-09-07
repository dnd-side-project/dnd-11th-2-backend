package com.dnd.runus.infrastructure.persistence.domain.scale;

import com.dnd.runus.domain.common.Coordinate;
import com.dnd.runus.domain.common.Pace;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.domain.scale.Scale;
import com.dnd.runus.domain.scale.ScaleRepository;
import com.dnd.runus.domain.scale.ScaleSummary;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.global.constant.RunningEmoji;
import com.dnd.runus.infrastructure.persistence.annotation.RepositoryTest;
import com.dnd.runus.infrastructure.persistence.jpa.scale.entity.ScaleEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    @BeforeEach
    void setUp() {
        // insert test data
        Scale scale1 = new Scale(0, "scale1", 1_000_000, 1, "서울(한국)", "도쿄(일본)");
        Scale scale2 = new Scale(0, "scale2", 2_100_000, 2, "도쿄(일본)", "베이징(중국)");
        Scale scale3 = new Scale(0, "scale3", 1_000_000, 3, "베이징(중국)", "타이베이(대만)");

        em.persist(ScaleEntity.from(scale1));
        em.persist(ScaleEntity.from(scale2));
        em.persist(ScaleEntity.from(scale3));
        em.flush();
    }

    @Transactional
    @DisplayName("지구 한바퀴 코스 조회:코스 수, 전체 코스 거리를 반환한다.")
    @Test
    void getSummary() {
        // when
        ScaleSummary summary = scaleRepository.getSummary();

        // then
        assertThat(summary.totalCourseCnt()).isEqualTo(3);
        assertThat(summary.totalCourseDistanceKm()).isEqualTo((4_100 * 1000));
    }

    @Transactional
    @DisplayName("성취 가능한 scale_id를 반환한다.")
    @Test
    void findAchievableScaleIds() {
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
        assertNotNull(achievableScaleIds.get(0));
        assertNotNull(achievableScaleIds.get(1));
    }
}
