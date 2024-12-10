package com.dnd.runus.application.challenge;

import com.dnd.runus.domain.challenge.ChallengeRepository;
import com.dnd.runus.domain.running.RunningRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    private final RunningRecordRepository runningRecordRepository;
}
