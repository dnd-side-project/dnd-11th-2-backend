package com.dnd.runus.infrastructure.persistence.jpa.member.entity;

import com.dnd.runus.domain.common.BaseTimeEntity;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberLevel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity(name = "member_level")
@NoArgsConstructor(access = PROTECTED)
public class MemberLevelEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(fetch = LAZY)
    private MemberEntity member;

    @NotNull
    private Long levelId;

    @NotNull
    private Integer exp;

    public static MemberLevelEntity of(Member member, long levelId, int exp) {
        MemberLevelEntity memberLevelEntity = new MemberLevelEntity();
        memberLevelEntity.member = MemberEntity.from(member);
        memberLevelEntity.levelId = levelId;
        memberLevelEntity.exp = exp;
        return memberLevelEntity;
    }

    public MemberLevel toDomain() {
        return new MemberLevel(levelId, exp);
    }
}
