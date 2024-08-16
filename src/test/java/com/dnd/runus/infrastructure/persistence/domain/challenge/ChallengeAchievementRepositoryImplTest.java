package com.dnd.runus.infrastructure.persistence.domain.challenge;

import com.dnd.runus.domain.challenge.ChallengeAchievement;
import com.dnd.runus.domain.challenge.ChallengeAchievementRecord;
import com.dnd.runus.domain.challenge.ChallengeAchievementRepository;
import com.dnd.runus.domain.challenge.ChallengePercentageValues;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.infrastructure.persistence.annotation.RepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RepositoryTest
public class ChallengeAchievementRepositoryImplTest {

    @Autowired
    private ChallengeAchievementRepository challengeAchievementRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("사용자 챌린지 성취 저장: 퍼센테이지 바 있는 경우")
    @Test
    void saveAchievementWithPercentage() {
        // given
        Member savedMember = memberRepository.save(new Member(MemberRole.USER, "nickname"));
        long runningId = 1L;
        long challengeId = 1L;
        ChallengeAchievementRecord record =
                new ChallengeAchievementRecord(true, true, new ChallengePercentageValues(3000, 0, 1000));
        ChallengeAchievement challengeAchievement =
                new ChallengeAchievement(savedMember, runningId, challengeId, record);

        // when
        ChallengeAchievement saved = challengeAchievementRepository.save(challengeAchievement);

        // then
        assertNotNull(saved);
        assertTrue(saved.record().successStatus());
        assertTrue(saved.record().hasPercentage());
        assertNotNull(saved.record().percentageValues());

        ChallengePercentageValues percentageValues = saved.record().percentageValues();
        assertThat(percentageValues.myValue()).isEqualTo(3000);
        assertThat(percentageValues.startValue()).isEqualTo(0);
        assertThat(percentageValues.endValue()).isEqualTo(1000);
        assertThat(percentageValues.percentage()).isEqualTo(100);
    }

    @DisplayName("사용자 챌린지 성취 저장: 퍼센테이지 바 없는 경우")
    @Test
    void saveAchievementWithoutPercentage() {
        // given
        Member savedMember = memberRepository.save(new Member(MemberRole.USER, "nickname"));
        long runningId = 1L;
        long challengeId = 1L;
        ChallengeAchievementRecord record = new ChallengeAchievementRecord(true, false, null);
        ChallengeAchievement challengeAchievement =
                new ChallengeAchievement(savedMember, runningId, challengeId, record);

        // when
        ChallengeAchievement saved = challengeAchievementRepository.save(challengeAchievement);

        // then
        assertNotNull(saved);
        assertTrue(saved.record().successStatus());
        assertFalse(saved.record().hasPercentage());
        assertNull(saved.record().percentageValues());
    }
}
