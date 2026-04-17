package com.camel.clinic.dto.doctor;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorScheduleResponseDTO {
    private UUID id;
    private Integer dayOfWeek;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "HH:mm:ss",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date startTime;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "HH:mm:ss",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date endTime;

    private Integer slotDuration;
    private Integer maxPatientsPerSlot;
    private String location;
    private Boolean isActive;
}

