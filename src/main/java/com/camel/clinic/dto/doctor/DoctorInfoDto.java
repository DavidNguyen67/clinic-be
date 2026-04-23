package com.camel.clinic.dto.doctor;

import com.camel.clinic.entity.Specialty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class DoctorInfoDto {
    private String id;
    private String degree;
    private Integer experienceYears;
    private Integer totalReviews;
    private String education;
    private BigDecimal averageRating;
    private String fullName;
    private String pathAvatar;
    private BigDecimal consultationFee;
    private String bio;
    private boolean availableToday;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "HH:mm",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date nextSlot;

    private String workplace;
    private Specialty specialty;
}
