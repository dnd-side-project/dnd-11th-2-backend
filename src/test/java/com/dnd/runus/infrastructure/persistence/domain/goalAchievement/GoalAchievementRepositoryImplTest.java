package com.dnd.runus.infrastructure.persistence.domain.goalAchievement;

import com.dnd.runus.domain.challenge.GoalType;
import com.dnd.runus.domain.common.Coordinate;
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
import java.time.OffsetDateTime;
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

    private final int runningDistance = 5000;
    private final Duration runningDuration = Duration.ofHours(1);

    @BeforeEach
    void setUp() {
        Member savedMember = memberRepository.save(new Member(MemberRole.USER, "nickname"));
        runningRecord = runningRecordRepository.save(new RunningRecord(
                1,
                savedMember,
                runningDistance,
                runningDuration,
                1,
                new Pace(5, 11),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                "start location",
                "end location",
                RunningEmoji.SOSO));
    }

    @DisplayName("목표 성취 저장: 목표 성취함(거리)")
    @Test
    void saveGoalAchievementDistanceSuccess() {
        // given
        GoalAchievement goalAchievement = new GoalAchievement(runningRecord, GoalType.DISTANCE, runningDistance);

        // when
        GoalAchievement saved = goalAchievementRepository.save(goalAchievement);

        // then
        assertTrue(saved.isAchieved());
    }

    @DisplayName("목표 성취 저장: 목표 성취 실패함(거리)")
    @Test
    void saveGoalAchievementDistanceFailed() {
        // given
        GoalAchievement goalAchievement = new GoalAchievement(runningRecord, GoalType.DISTANCE, runningDistance + 1);

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
        GoalAchievement goalAchievement = new GoalAchievement(runningRecord, GoalType.TIME, goalTime);

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
        GoalAchievement goalAchievement = new GoalAchievement(runningRecord, GoalType.TIME, goalTime);

        // when
        GoalAchievement saved = goalAchievementRepository.save(goalAchievement);

        // then
        assertFalse(saved.isAchieved());
    }
}
