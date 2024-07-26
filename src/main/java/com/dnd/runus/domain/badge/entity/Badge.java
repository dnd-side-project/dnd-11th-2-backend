package com.dnd.runus.domain.badge.entity;

import com.dnd.runus.global.constant.BadgeType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class Badge {
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
    private String imagePath;

    @NotNull
    @Enumerated(STRING)
    private BadgeType type;

    @NotNull
    private Integer requiredValue;

    public static Badge of(String name, String description, String imagePath, BadgeType type, Integer requiredValue) {
        Badge badge = new Badge();
        badge.name = name;
        badge.description = description;
        badge.imagePath = imagePath;
        badge.type = type;
        badge.requiredValue = requiredValue;
        return badge;
    }
}
