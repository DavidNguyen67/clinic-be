package com.camel.clinic.dto.doctor;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    private Integer dayOfWeek; // 0 = Sunday, 1 = Monday, ..., 6 = Saturday

    @NotNull(message = "Start time is required")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "HH:mm:ss",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date startTime;

    @NotNull(message = "End time is required")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "HH:mm:ss",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date endTime;

    @Positive(message = "Slot duration must be greater than 0")
    private Integer slotDuration = 30; // minutes

    @Positive(message = "Max patients per slot must be greater than 0")
    private Integer maxPatientsPerSlot = 1;

    private String location;

    private Boolean isActive = true;
}

