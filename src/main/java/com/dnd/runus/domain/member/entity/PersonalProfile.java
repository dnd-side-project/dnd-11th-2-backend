package com.dnd.runus.domain.member.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@Embeddable
public class PersonalProfile {
    @Enumerated(STRING)
    private Gender gender;

    private Integer heightCm;

    private Integer weightKg;

    private LocalDate birthDay;

    public enum Gender {
        MALE,
        FEMALE
    }
}
