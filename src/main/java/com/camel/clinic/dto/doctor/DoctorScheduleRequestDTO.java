package com.camel.clinic.dto.doctor;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorScheduleRequestDTO {

    @NotNull(message = "Day of week is required")
    @Min(value = 0, message = "Day of week must be between 0-6")
    @Max(value = 6, message = "Day of week must be between 0-6")
    private Integer dayOfWeek;

    @NotNull(message = "Start time is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date startTime;

    @NotNull(message = "End time is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date endTime;

    @Positive(message = "Slot duration must be greater than 0")
    private Integer slotDuration = 30;

    @Positive(message = "Max patients per slot must be greater than 0")
    private Integer maxPatientsPerSlot = 1;

    private String location;
    private Boolean isActive = true;

    @JsonIgnore
    @AssertTrue(message = "End time must be after start time")
    public boolean isValidTimeRange() {
        if (startTime == null || endTime == null) return true;
        return endTime.after(startTime);
    }
}