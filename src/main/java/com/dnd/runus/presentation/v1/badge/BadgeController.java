package com.dnd.runus.presentation.v1.badge;

import com.dnd.runus.application.badge.BadgeService;
import com.dnd.runus.presentation.annotation.MemberId;
import com.dnd.runus.presentation.v1.badge.dto.response.AchievedBadgesResponse;
import com.dnd.runus.presentation.v1.badge.dto.response.AllBadgesListResponse;
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

    @GetMapping("/me/lists")
    @Operation(
            summary = "나의 뱃지 조회 목록 조회",
            description =
                    """
        서비스의 전체 뱃지를 조회 합니다.<br>
        최근 일주일에 사용자가 획득한 뱃지는 recencyBadges 리스트에 리턴됩니다.<br>
        뱃지의 카테고리 별 리스트로 뱃지가 리턴됩니다.<br>
        자세한 응답 구조는 AllBadgesListResponse의 Schema을 확인해주세요.<br>
        """)
    public AllBadgesListResponse getAllBadgesList(@MemberId long memberId) {
        return badgeService.getListOfAllBadges(memberId);
    }
}
