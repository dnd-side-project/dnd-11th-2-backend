package com.dnd.runus.domain.member;

import com.dnd.runus.global.constant.SocialType;

import java.util.Optional;

public interface SocialProfileRepository {
    Optional<SocialProfile> findById(Long socialProfileId);

    Optional<SocialProfile> findBySocialTypeAndOauthId(SocialType socialType, String oauthId);

    SocialProfile save(SocialProfile socialProfile);
}
