package com.dnd.runus.infrastructure.persistence.jpa.member.entity;

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

    public static MemberEntity from(Member member) {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.id = member.memberId() == 0 ? null : member.memberId();
        memberEntity.role = member.role();
        memberEntity.nickname = member.nickname();
        return memberEntity;
    }

    public Member toDomain() {
        return new Member(id, role, nickname, getCreatedAt(), getUpdatedAt());
    }
}
