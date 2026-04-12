package com.camel.clinic.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "services", indexes = {
        @Index(name = "idx_slug", columnList = "slug"),
        @Index(name = "idx_specialty_id", columnList = "specialty_id"),
        @Index(name = "idx_is_featured", columnList = "is_featured"),
        @Index(name = "idx_is_active", columnList = "is_active")
})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Service extends SoftDeletableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id", foreignKey = @ForeignKey(name = "fk_service_specialty"))
    private Specialty specialty;

    @NotBlank()
    @Column(nullable = false, length = 255)
    private String name;

    @NotBlank()
    @Column(unique = true, nullable = false, length = 255)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull()
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "promotional_price", precision = 10, scale = 2)
    private BigDecimal promotionalPrice;

    @Column(nullable = false)
    private Integer duration = 30; // minutes

    @Column(length = 500)
    private String image;

    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
