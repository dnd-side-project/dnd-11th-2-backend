package com.dnd.runus.infrastructure.persistence.jpa.member.entity;

import com.dnd.runus.domain.badge.Badge;
import com.dnd.runus.domain.common.BaseTimeEntity;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.global.constant.MemberRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity(name = "member")
@NoArgsConstructor(access = PROTECTED)
public class MemberEntity extends BaseTimeEntity {
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

    public static MemberEntity from(Member member) {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.id = member.memberId();
        memberEntity.role = member.role();
        memberEntity.nickname = member.nickname();
        memberEntity.mainBadgeId =
                member.mainBadge() == null ? null : member.mainBadge().badgeId();
        return memberEntity;
    }

    public Member toDomain() {
        return toDomain(null);
    }

    public Member toDomain(Badge mainBadge) {
        return new Member(id, getCreatedAt(), getUpdatedAt(), role, nickname, mainBadge);
    }
}
