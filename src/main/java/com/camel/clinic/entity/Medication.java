package com.camel.clinic.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "medications", indexes = {
        @Index(name = "idx_name", columnList = "name"),
        @Index(name = "idx_category", columnList = "category"),
        @Index(name = "idx_is_active", columnList = "is_active")
})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Medication extends SoftDeletableEntity {

    @NotBlank()
    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "generic_name", length = 255)
    private String genericName;

    @Column(length = 100)
    private String category;

    @Column(length = 100)
    private String form;

    @Column(length = 100)
    private String strength;

    @Column(length = 50)
    private String unit;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
