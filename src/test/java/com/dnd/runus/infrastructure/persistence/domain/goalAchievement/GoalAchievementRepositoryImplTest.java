package com.dnd.runus.infrastructure.persistence.domain.goalAchievement;

import com.dnd.runus.domain.challenge.GoalMetricType;
import com.dnd.runus.domain.common.CoordinatePoint;
import com.dnd.runus.domain.common.Pace;
import com.dnd.runus.domain.goalAchievement.GoalAchievement;
import com.dnd.runus.domain.goalAchievement.GoalAchievementRepository;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.global.constant.RunningEmoji;
import com.dnd.runus.infrastructure.persistence.annotation.RepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RepositoryTest
public class GoalAchievementRepositoryImplTest {

    @Autowired
    private GoalAchievementRepository goalAchievementRepository;

    @Autowired
    private RunningRecordRepository runningRecordRepository;

    @Autowired
    private MemberRepository memberRepository;

    private RunningRecord runningRecord;
    private Member savedMember;

    private final int runningDistance = 5000;
    private final Duration runningDuration = Duration.ofHours(1);

    @BeforeEach
    void setUp() {
        savedMember = memberRepository.save(new Member(MemberRole.USER, "nickname"));
        runningRecord = runningRecordRepository.save(new RunningRecord(
                0,
                savedMember,
                runningDistance,
                runningDuration,
                1,
                new Pace(5, 11),
                ZonedDateTime.now(),
                ZonedDateTime.now(),
                List.of(new CoordinatePoint(1, 2, 3), new CoordinatePoint(4, 5, 6)),
                "start location",
                "end location",
                RunningEmoji.SOSO));
    }

    @DisplayName("목표 성취 저장: 목표 성취함(거리)")
    @Test
    void saveGoalAchievementDistanceSuccess() {
        // given
        GoalAchievement goalAchievement = new GoalAchievement(runningRecord, GoalMetricType.DISTANCE, runningDistance);

        // when
        GoalAchievement saved = goalAchievementRepository.save(goalAchievement);

        // then
        assertTrue(saved.isAchieved());
    }

    @DisplayName("목표 성취 저장: 목표 성취 실패함(거리)")
    @Test
    void saveGoalAchievementDistanceFailed() {
        // given
        GoalAchievement goalAchievement =
                new GoalAchievement(runningRecord, GoalMetricType.DISTANCE, runningDistance + 1);

        // when
        GoalAchievement saved = goalAchievementRepository.save(goalAchievement);

        // then
        assertFalse(saved.isAchieved());
    }

    @DisplayName("목표 성취 저장: 목표 성취함(시간)")
    @Test
    void saveGoalAchievementTimeSuccess() {
        // given
        int goalTime = (int) runningDuration.toSeconds();
        GoalAchievement goalAchievement = new GoalAchievement(runningRecord, GoalMetricType.TIME, goalTime);

        // when
        GoalAchievement saved = goalAchievementRepository.save(goalAchievement);

        // then
        assertTrue(saved.isAchieved());
    }

    @DisplayName("목표 성취 저장: 목표 성취 실패함(시간)")
    @Test
    void saveGoalAchievementTimeFailed() {
        // given
        int goalTime = (int) runningDuration.toSeconds() + 1;
        GoalAchievement goalAchievement = new GoalAchievement(runningRecord, GoalMetricType.TIME, goalTime);

        // when
        GoalAchievement saved = goalAchievementRepository.save(goalAchievement);

        // then
        assertFalse(saved.isAchieved());
    }

    @DisplayName("runningRecords로 삭제합니다.")
    @Test
    void deleteAllByRunningRecordIdsSuccess() {
        // given
        RunningRecord runningRecord2 = runningRecordRepository.save(new RunningRecord(
                0,
                savedMember,
                runningDistance,
                runningDuration,
                1,
                new Pace(5, 11),
                ZonedDateTime.now(),
                ZonedDateTime.now(),
                List.of(new CoordinatePoint(1, 2, 3), new CoordinatePoint(4, 5, 6)),
                "start location",
                "end location",
                RunningEmoji.SOSO));
        RunningRecord runningRecord3 = runningRecordRepository.save(new RunningRecord(
                0,
                savedMember,
                runningDistance,
                runningDuration,
                1,
                new Pace(5, 11),
                ZonedDateTime.now(),
                ZonedDateTime.now(),
                List.of(new CoordinatePoint(1, 2, 3), new CoordinatePoint(4, 5, 6)),
                "start location",
                "end location",
                RunningEmoji.SOSO));

        int goalTime = (int) runningDuration.toSeconds() + 1;
        GoalAchievement saved1 =
                goalAchievementRepository.save(new GoalAchievement(runningRecord, GoalMetricType.TIME, goalTime));
        GoalAchievement saved2 =
                goalAchievementRepository.save(new GoalAchievement(runningRecord2, GoalMetricType.TIME, goalTime));
        GoalAchievement saved3 =
                goalAchievementRepository.save(new GoalAchievement(runningRecord3, GoalMetricType.TIME, goalTime));
        List<RunningRecord> runningRecords =
                List.of(saved1.runningRecord(), saved2.runningRecord(), saved3.runningRecord());

        // when
        goalAchievementRepository.deleteByRunningRecords(runningRecords);

        // then
        assertFalse(goalAchievementRepository
                .findByRunningRecordId(saved1.runningRecord().runningId())
                .isPresent());
        assertFalse(goalAchievementRepository
                .findByRunningRecordId(saved2.runningRecord().runningId())
                .isPresent());
        assertFalse(goalAchievementRepository
                .findByRunningRecordId(saved3.runningRecord().runningId())
                .isPresent());
    }
}
