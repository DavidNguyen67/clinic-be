package com.camel.clinic.dto.patientProfile;

import com.camel.clinic.entity.PatientProfile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePatientProfileDto {
    @NotBlank(message = "User ID is required")
    private String userId;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @Size(max = 100, message = "Insurance number must not exceed 100 characters")
    private String insuranceNumber;

    @NotNull(message = "Blood type is required")
    private PatientProfile.BloodType bloodType;

    @Size(max = 2000, message = "Allergies must not exceed 2000 characters")
    private String allergies;

    @Size(max = 2000, message = "Chronic diseases must not exceed 2000 characters")
    private String chronicDiseases;
}