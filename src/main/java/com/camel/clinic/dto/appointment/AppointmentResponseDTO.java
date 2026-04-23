package com.camel.clinic.dto.appointment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
public class AppointmentResponseDTO {
    private UUID id;
    private String appointmentCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone = "Asia/Ho_Chi_Minh")
    private Date appointmentDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Ho_Chi_Minh")
    private Date startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Ho_Chi_Minh")
    private Date endTime;

    private String status;
    private String bookingType;
    private String reason;
    private String symptoms;
    private String notes;
    private Integer queueNumber;

    private UUID doctorId;
    private String doctorName;
    private UUID patientId;
    private String patientName;
    private UUID serviceId;
    private String serviceName;

    private BigDecimal price;

    private String patientPhone;
    private String patientEmail;
    private String doctorPhone;
    private String doctorEmail;
}

