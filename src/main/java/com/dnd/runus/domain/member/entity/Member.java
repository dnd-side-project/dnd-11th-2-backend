package com.dnd.runus.domain.member.entity;

import com.dnd.runus.domain.common.BaseTimeEntity;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.global.constant.SocialType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(STRING)
    private MemberRole role;

    @NotNull
    private String oauthId;

    @NotNull
    @Enumerated(STRING)
    private SocialType socialType;

    @NotNull
    private String oauthEmail;

    @Builder
    private Member(MemberRole role, String oauthId, SocialType socialType, String oauthEmail) {
        this.role = role;
        this.oauthId = oauthId;
        this.socialType = socialType;
        this.oauthEmail = oauthEmail;
    }
}
