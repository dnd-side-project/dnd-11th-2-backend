package com.dnd.runus.presentation.v1.challenge;

import com.dnd.runus.application.challenge.ChallengeService;
import com.dnd.runus.presentation.annotation.MemberId;
import com.dnd.runus.presentation.v1.challenge.dto.response.ChallengesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/challenge")
public class ChallengeController {

    @Autowired
    private final ChallengeService challengeService;

    @GetMapping
    public List<ChallengesResponse> getChallenges(@MemberId long memberId) {
        return challengeService.getChallenges(memberId);
    }
}
