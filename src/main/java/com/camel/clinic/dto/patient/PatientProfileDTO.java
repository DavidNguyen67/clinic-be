package com.camel.clinic.dto.patient;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PatientProfileDTO {
    private UUID id;
    private String patientCode;
    private String fullName;
    private String email;
    private String phone;
    private String gender;
    //    private String dateOfBirth;
    private String address;
    private String insuranceNumber;
    private String bloodType;
    private String allergies;
    private String chronicDiseases;
    private Integer loyaltyPoints;
    private Integer totalVisits;
}

