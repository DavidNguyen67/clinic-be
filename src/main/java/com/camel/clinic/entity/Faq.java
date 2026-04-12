package com.camel.clinic.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "faqs", indexes = {
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
