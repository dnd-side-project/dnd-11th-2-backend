package com.dnd.runus.domain.member.entity;

import com.dnd.runus.domain.common.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class MemberLevel extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long memberId;

    @NotNull
    private Long levelId;

    @NotNull
    private Integer exp;

    public static MemberLevel of(Long memberId, Long levelId, Integer exp) {
        MemberLevel memberLevel = new MemberLevel();
        memberLevel.memberId = memberId;
        memberLevel.levelId = levelId;
        memberLevel.exp = exp;
        return memberLevel;
    }
}
