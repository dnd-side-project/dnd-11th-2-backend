package com.dnd.runus.infrastructure.persistence.jpa.challenge.entity;

import com.dnd.runus.domain.challenge.Challenge;
import com.dnd.runus.domain.challenge.ChallengeType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity(name = "challenge")
@NoArgsConstructor(access = PROTECTED)
@Builder(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class ChallengeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private Integer expectedTime;

    @NotNull
    @Enumerated(STRING)
    private ChallengeType challengeType;

    @NotNull
    private String imageUrl;

    @NotNull
    private Boolean isActive;

    public static ChallengeEntity from(Challenge challenge) {
        return ChallengeEntity.builder()
                .id(challenge.challengeId() == 0 ? null : challenge.challengeId())
                .name(challenge.name())
                .expectedTime(challenge.expectedTime())
                .isActive(challenge.isActive())
                .challengeType(challenge.challengeType())
                .imageUrl(challenge.imageUrl())
                .build();
    }

    public Challenge toDomain() {
        return new Challenge(id, name, expectedTime, imageUrl, isActive, challengeType);
    }
}
