package com.dnd.runus.infrastructure.persistence.jpa.level.entity;

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
@Entity(name = "level")
@NoArgsConstructor(access = PROTECTED)
public class LevelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Integer expRangeStart;

    @NotNull
    private Integer expRangeEnd;

    @NotNull
    private String imageUrl;

    public static LevelEntity from(Level level) {
        LevelEntity levelEntity = new LevelEntity();
        levelEntity.expRangeStart = level.expRangeStart();
        levelEntity.expRangeEnd = level.expRangeEnd();
        levelEntity.imageUrl = level.imageUrl();
        return levelEntity;
    }

    public Level toDomain() {
        return new Level(id, expRangeStart, expRangeEnd, imageUrl);
    }
}
