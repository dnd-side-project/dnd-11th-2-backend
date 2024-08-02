package com.dnd.runus.infrastructure.persistence.jpa.badge.entity;

import com.dnd.runus.domain.badge.Badge;
import com.dnd.runus.domain.badge.BadgeAchievement;
import com.dnd.runus.domain.common.BaseTimeEntity;
import com.dnd.runus.infrastructure.persistence.jpa.member.entity.MemberEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity(name = "badge_achievement")
@NoArgsConstructor(access = PROTECTED)
public class BadgeAchievementEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long badgeId;

    @NotNull
    @ManyToOne(fetch = LAZY)
    private MemberEntity memberEntity;

    public static BadgeAchievementEntity from(BadgeAchievement badgeAchievement) {
        BadgeAchievementEntity badgeAchievementEntity = new BadgeAchievementEntity();
        badgeAchievementEntity.badgeId = badgeAchievement.badge().badgeId();
        badgeAchievementEntity.memberEntity = MemberEntity.from(badgeAchievement.member());
        return badgeAchievementEntity;
    }

    public BadgeAchievement toDomain(Badge badge) {
        return new BadgeAchievement(badge, memberEntity.toDomain(), getCreatedAt(), getUpdatedAt());
    }
}
