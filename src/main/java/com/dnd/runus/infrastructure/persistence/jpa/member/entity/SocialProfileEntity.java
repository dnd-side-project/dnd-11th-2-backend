package com.dnd.runus.infrastructure.persistence.jpa.member.entity;

import com.dnd.runus.domain.common.BaseTimeEntity;
import com.dnd.runus.domain.member.SocialProfile;
import com.dnd.runus.global.constant.SocialType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity(name = "social_profile")
@NoArgsConstructor(access = PROTECTED)
public class SocialProfileEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = LAZY)
    private MemberEntity member;

    @NotNull
    @Enumerated(STRING)
    private SocialType socialType;

    @NotNull
    private String oauthId;

    @NotNull
    private String oauthEmail;

    public static SocialProfileEntity from(SocialProfile socialProfile) {
        SocialProfileEntity socialProfileEntity = new SocialProfileEntity();
        socialProfileEntity.id = socialProfile.socialProfileId();
        socialProfileEntity.member = MemberEntity.from(socialProfile.member());
        socialProfileEntity.socialType = socialProfile.socialType();
        socialProfileEntity.oauthId = socialProfile.oauthId();
        socialProfileEntity.oauthEmail = socialProfile.oauthEmail();
        return socialProfileEntity;
    }

    public SocialProfile toDomain() {
        return new SocialProfile(id, member.toDomain(), socialType, oauthId, oauthEmail);
    }

    public void updateOauthEmail(String oauthEmail) {
        this.oauthEmail = oauthEmail;
    }
}
