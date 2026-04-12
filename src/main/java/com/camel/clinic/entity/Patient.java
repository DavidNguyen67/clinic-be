package com.camel.clinic.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "patients", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_patient_code", columnList = "patient_code")
})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Patient extends SoftDeletableEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false, foreignKey = @ForeignKey(name = "fk_patient_user"))
    @NotNull()
    private User user;

    @NotBlank()
    @Column(name = "patient_code", unique = true, nullable = false, length = 20)
    private String patientCode;

    @Column(name = "date_of_birth")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "insurance_number", length = 100)
    private String insuranceNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_type", length = 5)
    private BloodType bloodType;

    @Column(columnDefinition = "TEXT")
    private String allergies;

    @Column(name = "chronic_diseases", columnDefinition = "TEXT")
    private String chronicDiseases;

    @Column(name = "loyalty_points", nullable = false)
    private Integer loyaltyPoints = 0;

    @Column(name = "total_visits", nullable = false)
    private Integer totalVisits = 0;

    public enum Gender {
        male, female, other
    }

    public enum BloodType {
        A_POSITIVE("A+"),
        A_NEGATIVE("A-"),
        B_POSITIVE("B+"),
        B_NEGATIVE("B-"),
        AB_POSITIVE("AB+"),
        AB_NEGATIVE("AB-"),
        O_POSITIVE("O+"),
        O_NEGATIVE("O-");

        private final String displayName;

        BloodType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
