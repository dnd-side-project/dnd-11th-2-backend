package com.dnd.runus.presentation.v1.badge;

import com.dnd.runus.application.badge.BadgeService;
import com.dnd.runus.presentation.annotation.MemberId;
import com.dnd.runus.presentation.v1.badge.dto.response.AchievedBadgesResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/badges")
public class BadgeController {
    private final BadgeService badgeService;

    @GetMapping("/me")
    @Operation(summary = "자신이 획득한 뱃지 전체 조회", description = "자신이 획득한 뱃지를 전체 조회합니다.")
    public AchievedBadgesResponse getMyBadges(@MemberId long memberId) {
        return badgeService.getAchievedBadges(memberId);
    }
}
