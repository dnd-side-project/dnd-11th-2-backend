package com.dnd.runus.infrastructure.persistence.jpa.scale.entity;

import com.dnd.runus.domain.scale.Scale;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity(name = "scale")
@NoArgsConstructor(access = PROTECTED)
@Builder(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class ScaleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private Integer sizeMeter;

    @NotNull
    private Integer achievementValueMeter;

    @NotNull
    private Integer index;

    @NotNull
    @Size(max = 40)
    private String startName;

    @NotNull
    @Size(max = 40)
    private String endName;

    public Scale toDomain() {
        return new Scale(id, name, sizeMeter, index, achievementValueMeter, startName, endName);
    }

    public static ScaleEntity from(Scale scale) {
        return ScaleEntity.builder()
                .name(scale.name())
                .sizeMeter(scale.sizeMeter())
                .index(scale.index())
                .startName(scale.startName())
                .endName(scale.endName())
                .achievementValueMeter(scale.achievementValueMeter())
                .build();
    }
}
