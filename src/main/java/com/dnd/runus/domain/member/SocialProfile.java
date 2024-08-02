package com.dnd.runus.domain.member;

import com.dnd.runus.global.constant.SocialType;
import lombok.Builder;

@Builder
public record SocialProfile(
        long socialProfileId, SocialType socialType, String oauthId, String oauthEmail, long memberId) {}
