package com.camel.clinic.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "faq", indexes = {
        @Index(name = "idx_category", columnList = "category"),
        @Index(name = "idx_is_active", columnList = "is_active"),
        @Index(name = "idx_display_order", columnList = "display_order")
})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Faq extends SoftDeletableEntity {

    @Column(length = 100)
    private String category;

    @NotBlank()
    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @NotBlank()
    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
