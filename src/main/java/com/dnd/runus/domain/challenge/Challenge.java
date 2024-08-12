package com.dnd.runus.domain.challenge;

import lombok.Builder;

@Builder
public record Challenge(long challengeId, String name, int expectedTime, String imageUrl) {}
