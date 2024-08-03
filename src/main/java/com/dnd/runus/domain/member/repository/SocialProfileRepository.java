package com.dnd.runus.domain.member.repository;

import com.dnd.runus.domain.member.entity.SocialProfile;
import com.dnd.runus.global.constant.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialProfileRepository extends JpaRepository<SocialProfile, Long> {
    Optional<SocialProfile> findBySocialTypeAndOauthId(SocialType socialType, String oauthId);

    boolean existsByOauthEmail(String email);

    boolean existsBySocialTypeAndOauthId(SocialType socialType, String oauthId);
}
