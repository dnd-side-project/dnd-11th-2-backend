package com.dnd.runus.domain.challenge;

import static com.dnd.runus.global.constant.MetricsConversionFactor.SECONDS_PER_HOUR;
import static com.dnd.runus.global.constant.MetricsConversionFactor.SECONDS_PER_MINUTE;

public record Challenge(
        long challengeId,
        String name,
        int expectedTime,
        String imageUrl,
        boolean isActive,
        ChallengeType challengeType) {

    public Challenge(long challengeId, String name, String imageUrl, boolean isActive, ChallengeType challengeType) {
        this(challengeId, name, 0, imageUrl, isActive, challengeType);
    }

    public boolean isDefeatYesterdayChallenge() {
        return this.challengeType == ChallengeType.DEFEAT_YESTERDAY;
    }

    public String formatExpectedTime() {
        int hour = expectedTime / SECONDS_PER_HOUR;
        int minute = (expectedTime % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE;
        StringBuilder sb = new StringBuilder();

        if (hour != 0) {
            sb.append(hour).append("시간 ");
        }
        if (minute != 0) {
            sb.append(minute).append("분");
        }
        if (expectedTime == 0) {
            sb.append("0분");
        }

        return sb.toString().trim();
    }
}
