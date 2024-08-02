package com.dnd.runus.infrastructure.persistence.jpa.member;

import com.dnd.runus.global.constant.SocialType;
import com.dnd.runus.infrastructure.persistence.jpa.member.entity.SocialProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaSocialProfileRepository extends JpaRepository<SocialProfileEntity, Long> {
    Optional<SocialProfileEntity> findBySocialTypeAndOauthId(SocialType socialType, String oauthId);
}
