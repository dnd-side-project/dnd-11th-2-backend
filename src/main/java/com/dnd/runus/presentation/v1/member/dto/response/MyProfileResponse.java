package com.dnd.runus.presentation.v1.member.dto.response;


public record MyProfileResponse(
        String profileImageUrl,
        long currentLevel,
        int currentExpMeter,
        long nextLevel,
        int nextLevelStartExpMeter,
        int nextLevelEndExpMeter
) {
}
