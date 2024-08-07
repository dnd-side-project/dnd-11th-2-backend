package com.dnd.runus.infrastructure.persistence.domain.running;

import com.dnd.runus.domain.common.Coordinate;
import com.dnd.runus.domain.common.Pace;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.global.constant.RunningEmoji;
import com.dnd.runus.infrastructure.persistence.annotation.RepositoryTest;
import com.dnd.runus.infrastructure.persistence.jpa.running.JpaRunningRecordRepository;
import com.dnd.runus.infrastructure.persistence.jpa.running.entity.RunningRecordEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;

@RepositoryTest
class RunningRecordRepositoryImplTest {

    @Autowired
    private RunningRecordRepository runningRecordRepository;

    @Autowired
    private JpaRunningRecordRepository jpaRunningRecordRepository;

    @Autowired
    private MemberRepository memberRepository;

    private static Member savedMember;

    @BeforeEach
    void beforeEach() {
        Member member = new Member(MemberRole.USER, "nickname");
        savedMember = memberRepository.save(member);
    }

    @Test
    void deleteByMember() {
        // given
        RunningRecord runningRecord = new RunningRecord(
                0,
                savedMember,
                1,
                Duration.between(LocalTime.of(0, 0, 0), LocalTime.of(12, 23, 56)),
                1,
                new Pace(5, 11),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                new ArrayList<>() {
                    {
                        add(new Coordinate(1, 2, 3));
                        add(new Coordinate(4, 5, 6));
                    }
                },
                "location",
                RunningEmoji.BAD);
        RunningRecordEntity entity = RunningRecordEntity.from(runningRecord);
        RunningRecordEntity savedRunningRecord = jpaRunningRecordRepository.save(entity);

        // when
        runningRecordRepository.deleteByMember(savedMember);

        // then
        assertFalse(
                jpaRunningRecordRepository.findById(savedRunningRecord.getId()).isPresent());
    }
}
