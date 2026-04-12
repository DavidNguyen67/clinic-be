package com.camel.clinic.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "prescription_items", indexes = {
        @Index(name = "idx_prescription_id", columnList = "prescription_id"),
        @Index(name = "idx_medication_id", columnList = "medication_id")
})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class PrescriptionItem extends SoftDeletableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false, foreignKey = @ForeignKey(name = "fk_item_prescription"))
    @NotNull()
    @JsonBackReference("prescription-items")
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id", nullable = false, foreignKey = @ForeignKey(name = "fk_item_medication"))
    @NotNull()
    private Medication medication;

    @Column(length = 100)
    private String dosage;

    @Column(length = 100)
    private String frequency;

    @Column(length = 100)
    private String duration;

    @NotNull()
    @Column(nullable = false)
    private Integer quantity;

    @Column(columnDefinition = "TEXT")
    private String instructions;
}
