package com.dnd.runus.infrastructure.persistence.jpa.member.entity;

import com.dnd.runus.domain.common.BaseTimeEntity;
import com.dnd.runus.domain.level.Level;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity(name = "member_level")
@NoArgsConstructor(access = PROTECTED)
public class MemberLevelEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long memberId;

    @NotNull
    private Long levelId;

    @NotNull
    private Integer exp;

    public static MemberLevelEntity of(Long memberId, Long levelId, Integer exp) {
        MemberLevelEntity memberLevelEntity = new MemberLevelEntity();
        memberLevelEntity.memberId = memberId;
        memberLevelEntity.levelId = levelId;
        memberLevelEntity.exp = exp;
        return memberLevelEntity;
    }

    public Level toDomain() {
        return new Level(levelId, exp);
    }
}
