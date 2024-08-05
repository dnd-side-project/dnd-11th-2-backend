package com.dnd.runus.infrastructure.persistence.domain.member;

import com.dnd.runus.domain.member.SocialProfile;
import com.dnd.runus.domain.member.SocialProfileRepository;
import com.dnd.runus.global.constant.SocialType;
import com.dnd.runus.infrastructure.persistence.jpa.member.JpaSocialProfileRepository;
import com.dnd.runus.infrastructure.persistence.jpa.member.entity.SocialProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SocialProfileRepositoryImpl implements SocialProfileRepository {
    private final JpaSocialProfileRepository jpaSocialProfileRepository;

    @Override
    public Optional<SocialProfile> findById(Long socialProfileId) {
        return jpaSocialProfileRepository.findById(socialProfileId).map(SocialProfileEntity::toDomain);
    }

    @Override
    public Optional<SocialProfile> findBySocialTypeAndOauthId(SocialType socialType, String oauthId) {
        return jpaSocialProfileRepository
                .findBySocialTypeAndOauthId(socialType, oauthId)
                .map(SocialProfileEntity::toDomain);
    }

    @Override
    public SocialProfile save(SocialProfile socialProfile) {
        SocialProfileEntity entity = jpaSocialProfileRepository.save(SocialProfileEntity.of(
                socialProfile.socialType(),
                socialProfile.oauthId(),
                socialProfile.oauthEmail(),
                socialProfile.memberId()));
        return entity.toDomain();
    }

    @Override
    public void updateOauthEmail(long socialProfileId, String oauthEmail) {
        jpaSocialProfileRepository
                .findById(socialProfileId)
                .ifPresent(socialProfileEntity -> socialProfileEntity.updateOauthEmail(oauthEmail));
    }
}
