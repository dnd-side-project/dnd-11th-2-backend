package com.dnd.runus.domain.badge.entity;

import com.dnd.runus.domain.member.entity.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class BadgeAchievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = LAZY)
    private Badge badge;

    @NotNull
    @ManyToOne(fetch = LAZY)
    private Member member;

    public static BadgeAchievement of(Badge badge, Member member) {
        BadgeAchievement badgeAchievement = new BadgeAchievement();
        badgeAchievement.badge = badge;
        badgeAchievement.member = member;
        return badgeAchievement;
    }
}
