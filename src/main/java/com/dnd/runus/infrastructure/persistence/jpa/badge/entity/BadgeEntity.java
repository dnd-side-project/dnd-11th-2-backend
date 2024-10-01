package com.dnd.runus.infrastructure.persistence.jpa.badge.entity;

import com.dnd.runus.domain.badge.Badge;
import com.dnd.runus.global.constant.BadgeType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity(name = "badge")
@NoArgsConstructor(access = PROTECTED)
public class BadgeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 20)
    private String name;

    @NotNull
    @Size(max = 100)
    private String description;

    @NotNull
    @Size(max = 500)
    private String imageUrl;

    @NotNull
    @Enumerated(STRING)
    private BadgeType type;

    @NotNull
    private Integer requiredValue;

    public static BadgeEntity from(Badge badge) {
        BadgeEntity badgeEntity = new BadgeEntity();
        badgeEntity.id = badge.badgeId() == 0 ? null : badge.badgeId();
        badgeEntity.name = badge.name();
        badgeEntity.description = badge.description();
        badgeEntity.imageUrl = badge.imageUrl();
        badgeEntity.type = badge.type();
        badgeEntity.requiredValue = badge.requiredValue();
        return badgeEntity;
    }

    public Badge toDomain() {
        return new Badge(id, name, description, imageUrl, type, requiredValue);
    }
}
