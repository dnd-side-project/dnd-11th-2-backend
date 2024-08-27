package com.dnd.runus.domain.challenge;

public record Challenge(
        long challengeId, String name, String expectedTime, String imageUrl, ChallengeType challengeType) {
    public Challenge(long challengeId, String name, int expectedTime, String imageUrl, ChallengeType challengeType) {
        this(challengeId, name, expectedTimeFormat(expectedTime), imageUrl, challengeType);
    }

    public Challenge(long challengeId, String name, String imageUrl, ChallengeType challengeType) {
        this(challengeId, name, null, imageUrl, challengeType);
    }

    public boolean isDefeatYesterdayChallenge() {
        return this.challengeType == ChallengeType.DEFEAT_YESTERDAY;
    }

    private static String expectedTimeFormat(int expectedTime) {
        int hour = expectedTime / 3600;
        int minute = (expectedTime % 3600) / 60;
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
