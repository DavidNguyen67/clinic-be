package com.camel.clinic.dto.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileDTO {
    private UUID id;
    private String email;
    private String fullName;
    private String phone;
    private String role;
    private String gender;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date dateOfBirth;

    private String pathAvatar;
    private String status;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "HH:mm:ss dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date lastLogin;

    // Doctor specific fields
    private DoctorProfileDTO doctor;

    // Patient specific fields
    private PatientProfileDTO patient;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DoctorProfileDTO {
        private UUID id;
        private String doctorCode;
        private Integer experienceYears;
        private String degree;
        private String education;
        private String bio;
        private Double consultationFee;
        private Double averageRating;
        private Integer totalReviews;
        private Integer totalPatients;
        private Boolean isFeatured;
        private String status;

        // Specialty
        private SpecialtyDTO specialty;

        private List<ScheduleDTO> schedules;


        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class SpecialtyDTO {
            private UUID id;
            private String name;
            private String slug;
            private String description;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ScheduleDTO {
            private UUID id;
            private Integer dayOfWeek;
            private String startTime;
            private String endTime;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PatientProfileDTO {
        private UUID id;
        private String patientCode;
        private String address;
        private String insuranceNumber;
        private String bloodType;
        private String allergies;
        private String chronicDiseases;
        private Integer loyaltyPoints;
        private Integer totalVisits;
    }
}

