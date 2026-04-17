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
public class DoctorLeaveResponseDTO {
    private UUID id;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date leaveDate;

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

    private String reason;
    private String status;
    private String doctorName;
    private UUID doctorId;
}

