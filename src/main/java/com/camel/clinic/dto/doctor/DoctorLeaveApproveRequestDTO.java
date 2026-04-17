package com.camel.clinic.dto.doctor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorLeaveApproveRequestDTO {

    @NotNull(message = "Status is required")
    @NotBlank(message = "Status cannot be blank")
    private String status; // "approved" or "rejected"

    private String rejectionReason;
}

