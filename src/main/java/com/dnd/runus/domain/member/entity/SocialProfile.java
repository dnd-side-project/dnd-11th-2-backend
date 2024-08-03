package com.dnd.runus.domain.member.entity;

import com.dnd.runus.domain.common.BaseTimeEntity;
import com.dnd.runus.global.constant.SocialType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class SocialProfile extends BaseTimeEntity {
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

    public static SocialProfile of(SocialType socialType, String oauthId, String oauthEmail, Long memberId) {
        SocialProfile socialProfile = new SocialProfile();
        socialProfile.socialType = socialType;
        socialProfile.oauthId = oauthId;
        socialProfile.oauthEmail = oauthEmail;
        socialProfile.memberId = memberId;
        return socialProfile;
    }

    public void updateEmail(String email) {
        System.out.println("이메일");
        this.oauthEmail = email;
    }
}
