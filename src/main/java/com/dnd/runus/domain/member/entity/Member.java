package com.dnd.runus.domain.member.entity;

import com.dnd.runus.domain.common.BaseTimeEntity;
import com.dnd.runus.global.constant.MemberRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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
    @Size(max = 20)
    private String nickname;

    private Long mainBadgeId;

    @NotNull
    private Integer exp;

    @Embedded
    @Accessors(fluent = true)
    private SocialProfile social;

    @Embedded
    @Accessors(fluent = true)
    private PersonalProfile personal;

    public static Member of(MemberRole role, String nickname, SocialProfile social, PersonalProfile personal) {
        Member member = new Member();
        member.role = role;
        member.nickname = nickname;
        member.social = social;
        member.personal = personal;
        member.exp = 0;
        return member;
    }
}
