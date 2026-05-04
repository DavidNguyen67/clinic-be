package com.camel.clinic.entity;


import com.camel.clinic.converter.BloodTypeConverter;
import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "patient_profile", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_patient_code", columnList = "patient_code")
})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class PatientProfile extends SoftDeletableEntity {

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", unique = true, nullable = false, foreignKey = @ForeignKey(name = "fk_patient_user"))
    @NotNull()
    private User user;

    @NotBlank()
    @Column(name = "patient_code", unique = true, nullable = false, length = 20)
    private String patientCode;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "insurance_number", length = 100)
    private String insuranceNumber;

    @Convert(converter = BloodTypeConverter.class)
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

    @Getter
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

        @JsonCreator
        public static BloodType fromValue(String value) {
            for (BloodType bt : values()) {
                if (bt.displayName.equalsIgnoreCase(value) || bt.name().equalsIgnoreCase(value)) {
                    return bt;
                }
            }
            throw new IllegalArgumentException("Unknown blood type: " + value);
        }
    }
}
