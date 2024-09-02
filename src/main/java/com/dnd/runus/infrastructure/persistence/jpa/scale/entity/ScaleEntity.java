package com.dnd.runus.infrastructure.persistence.jpa.scale.entity;

import com.dnd.runus.infrastructure.persistence.domain.scale.Scale;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity(name = "scale")
@NoArgsConstructor(access = PROTECTED)
public class ScaleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private Integer sizeMeter;

    @NotNull
    private Integer index;

    @NotNull
    @Size(max = 40)
    private String startName;

    @NotNull
    @Size(max = 40)
    private String endName;

    public Scale toDomain() {
        return new Scale(id, name, sizeMeter, index, startName, endName);
    }
}
