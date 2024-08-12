package com.dnd.runus.application.challenge;

import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.infrastructure.persistence.jpa.challenge.entity.ChallengeData;
import com.dnd.runus.presentation.v1.challenge.dto.response.ChallengesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.dnd.runus.global.constant.TimeConstant.SERVER_TIMEZONE_ID;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final RunningRecordRepository runningRecordRepository;

    public List<ChallengesResponse> getChallenges(long memberId) {
        OffsetDateTime todayMidnight = LocalDate.now(SERVER_TIMEZONE_ID)
                .atStartOfDay(SERVER_TIMEZONE_ID)
                .toOffsetDateTime();
        OffsetDateTime yesterday = todayMidnight.minusDays(1);

        List<RunningRecord> runningRecords =
                runningRecordRepository.findByMemberIdAndStartAtBetween(memberId, yesterday, todayMidnight);

        return ChallengeData.getChallenges(!runningRecords.isEmpty()).stream()
                .map(ChallengesResponse::from)
                .collect(Collectors.toList());
    }
}
