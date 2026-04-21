package com.camel.clinic.dto.doctor;

import com.camel.clinic.entity.DoctorLeave;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorLeaveApproveRequestDTO {

    @NotNull(message = "Status is required")
    private DoctorLeave.LeaveStatus status;

    private String rejectionReason;

    @AssertTrue(message = "Rejection reason is required when status is rejected")
    public boolean isValidReason() {
        if (status == DoctorLeave.LeaveStatus.rejected) {
            return rejectionReason != null && !rejectionReason.trim().isEmpty();
        }
        return true;
    }
}