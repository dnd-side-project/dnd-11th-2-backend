package com.dnd.runus.infrastructure.persistence.jpa.member.entity;

import com.dnd.runus.domain.common.BaseTimeEntity;
import com.dnd.runus.domain.member.SocialProfile;
import com.dnd.runus.global.constant.SocialType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity(name = "social_profile")
@NoArgsConstructor(access = PROTECTED)
public class SocialProfileEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(STRING)
    private SocialType socialType;

    @NotNull
    private String oauthId;

    @NotNull
    private String oauthEmail;

    @NotNull
    private Long memberId;

    public static SocialProfileEntity of(SocialType socialType, String oauthId, String oauthEmail, Long memberId) {
        SocialProfileEntity socialProfileEntity = new SocialProfileEntity();
        socialProfileEntity.socialType = socialType;
        socialProfileEntity.oauthId = oauthId;
        socialProfileEntity.oauthEmail = oauthEmail;
        socialProfileEntity.memberId = memberId;
        return socialProfileEntity;
    }

    public SocialProfile toDomain() {
        return new SocialProfile(id, socialType, oauthId, oauthEmail, memberId);
    }
}
