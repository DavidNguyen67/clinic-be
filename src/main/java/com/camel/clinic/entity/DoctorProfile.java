package com.camel.clinic.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "doctor_profile", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_doctor_code", columnList = "doctor_code"),
        @Index(name = "idx_specialty_id", columnList = "specialty_id"),
        @Index(name = "idx_is_featured", columnList = "is_featured"),
        @Index(name = "idx_status", columnList = "status")
})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class DoctorProfile extends SoftDeletableEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false, foreignKey = @ForeignKey(name = "fk_doctor_user"))
    @NotNull()
    private User user;

    @NotBlank()
    @Column(name = "doctor_code", unique = true, nullable = false, length = 20)
    private String doctorCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id", nullable = false, foreignKey = @ForeignKey(name = "fk_doctor_specialty"))
    @NotNull()
    private Specialty specialty;

    @Column(length = 100)
    private String degree;

    @Column(name = "experience_years", nullable = false)
    private Integer experienceYears = 0;

    @Column(columnDefinition = "TEXT")
    private String education;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "consultation_fee", precision = 10, scale = 2, nullable = false)
    private BigDecimal consultationFee = BigDecimal.ZERO;

    @Column(name = "average_rating", precision = 3, scale = 2, nullable = false)
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(name = "total_reviews", nullable = false)
    private Integer totalReviews = 0;

    @Column(name = "total_patients", nullable = false)
    private Integer totalPatients = 0;

    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;
}
