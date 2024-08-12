package com.dnd.runus.presentation.v1.challenge;

import com.dnd.runus.application.challenge.ChallengeService;
import com.dnd.runus.presentation.annotation.MemberId;
import com.dnd.runus.presentation.v1.challenge.dto.response.ChallengesResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "오늘의 챌린지")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/challenges")
public class ChallengeController {

    @Autowired
    private final ChallengeService challengeService;

    @Operation(
            summary = "오늘의 챌린지 리스트 조회",
            description =
                    """
            ## 오늘의 챌린지 리스트를 조회합니다.
            - 챌린지가 페이스와 관련된 경우 응답의 예상 소요 시간은 0으로 리턴됩니다.
            - 사용자가 어제의 러닝 기록이 없는 경우 : 어제의 기록과 관련된 챌린지는 제외하고 반환합니다.
            - 사용자가 어제의 러닝 기록이 있는 경우 : 모든 챌린지를 반환합니다.
            """)
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ChallengesResponse> getChallenges(@MemberId long memberId) {
        return challengeService.getChallenges(memberId);
    }
}
