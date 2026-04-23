package com.camel.clinic.dto.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
public class ScheduleResponse {
    private UUID id;
    private Integer dayOfWeek;
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "HH:mm",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date startTime;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "HH:mm",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date endTime;
    private Integer slotDuration;
    private Integer maxPatientsPerSlot;
    private String location;
    private Boolean isActive;
}