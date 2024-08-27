package com.dnd.runus.domain.challenge;

import java.util.List;

public record ChallengeWithCondition(Challenge challengeInfo, List<ChallengeCondition> challengeConditions) {}
