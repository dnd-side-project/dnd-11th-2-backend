package com.dnd.runus.domain.challenge.achievement.dto;

public record PercentageValues(int achievementValue, int startValue, int endValue) {
    // todo 나중에 조회 기능 생기면 사용
    private static int calPercentage(int achievementValue, int startValue, int endValue) {
        int percent = (int) Math.floor(((double) (achievementValue) / (endValue - startValue)) * 100);
        return Math.min(percent, 100);
    }
}
