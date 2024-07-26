package com.dnd.runus.domain.member.entity;

import com.dnd.runus.global.constant.SocialType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@Embeddable
public class SocialProfile {
    @NotNull
    @Enumerated(STRING)
    private SocialType socialType;

    @NotNull
    private String oauthId;

    @NotNull
    private String oauthEmail;
}
