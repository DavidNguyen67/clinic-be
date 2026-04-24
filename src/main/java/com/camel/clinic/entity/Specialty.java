package com.camel.clinic.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "specialties", indexes = {
        @Index(name = "idx_slug", columnList = "slug"),
        @Index(name = "idx_is_active", columnList = "is_active")
})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Specialty extends SoftDeletableEntity {

    @NotBlank()
    @Column(nullable = false, length = 255)
    private String name;

    @NotBlank()
    @Column(unique = true, nullable = false, length = 255)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "specialty", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ClinicService> services = new ArrayList<>();

    @Column(length = 500)
    private String image;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @NotNull
    @Column(nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'GENERAL'")
    @Enumerated(EnumType.STRING)
    private SpecialtyType specialtyType;

    @OneToMany(mappedBy = "specialty", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("specialty-items")
    @JsonIgnore
    private List<Doctor> doctors = new ArrayList<>();

    public enum SpecialtyType {
        GENERAL,
        SURGERY,
        PEDIATRICS,
        DERMATOLOGY,
        CARDIOLOGY,
        ORTHOPEDICS,
        NEUROLOGY,
        PSYCHIATRY,
        GYNECOLOGY,
        ENDOCRINOLOGY
    }
}
