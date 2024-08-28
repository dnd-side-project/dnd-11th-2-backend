package com.dnd.runus.infrastructure.persistence.domain.challenge;

import com.dnd.runus.domain.challenge.Challenge;
import com.dnd.runus.domain.challenge.ChallengeRepository;
import com.dnd.runus.domain.challenge.ChallengeType;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievement;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRepository;
import com.dnd.runus.domain.common.Coordinate;
import com.dnd.runus.domain.common.Pace;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RepositoryTest
public class ChallengeAchievementRepositoryImplTest {

    @Autowired
    private ChallengeAchievementRepository challengeAchievementRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RunningRecordRepository runningRecordRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    private RunningRecord runningRecord;
    private Challenge challenge;

    @BeforeEach
    void setUp() {
        Member savedMember = memberRepository.save(new Member(MemberRole.USER, "nickname"));
        runningRecord = runningRecordRepository.save(new RunningRecord(
                1,
                savedMember,
                1,
                Duration.ofHours(12).plusMinutes(23).plusSeconds(56),
                1,
                new Pace(5, 11),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                "start location",
                "end location",
                RunningEmoji.SOSO));
        challenge = challengeRepository.save(new Challenge(0, "name", 60, "imageUrl", ChallengeType.DEFEAT_YESTERDAY));
    }

    @DisplayName("ChallengeAchievement 저장시, 성공여부가 true인지 확인")
    @Test
    void saveAchievement() {
        // given
        ChallengeAchievement challengeAchievement = new ChallengeAchievement(challenge, runningRecord, true);

        // when
        ChallengeAchievement saved = challengeAchievementRepository.save(challengeAchievement);

        // then
        assertNotNull(saved);
        assertTrue(saved.isSuccess());
    }
}
