package com.dnd.runus.infrastructure.persistence.jpa.scale.entity;

import com.dnd.runus.domain.common.BaseTimeEntity;
import com.dnd.runus.domain.scale.ScaleAchievement;
import com.dnd.runus.infrastructure.persistence.jpa.member.entity.MemberEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity(name = "scale_achievement")
@NoArgsConstructor(access = PROTECTED)
public class ScaleAchievementEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = LAZY)
    private MemberEntity member;

    @NotNull
    @ManyToOne(fetch = LAZY)
    private ScaleEntity scale;

    @NotNull
    private OffsetDateTime achievedDate;

    public static ScaleAchievementEntity from(ScaleAchievement scaleAchievement) {
        ScaleAchievementEntity scaleAchievementEntity = new ScaleAchievementEntity();
        scaleAchievementEntity.id = scaleAchievement.id() == 0 ? null : scaleAchievement.id();
        scaleAchievementEntity.member = MemberEntity.from(scaleAchievement.member());
        scaleAchievementEntity.scale = ScaleEntity.from(scaleAchievement.scale());
        scaleAchievementEntity.achievedDate = scaleAchievement.achievedDate();
        return scaleAchievementEntity;
    }

    public ScaleAchievement toDomain() {
        return new ScaleAchievement(id, member.toDomain(), scale.toDomain(), achievedDate);
    }
}
