package com.camel.clinic.dto;

import com.camel.clinic.entity.DoctorSchedule;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class DoctorWithScheduleDTO extends DoctorDTO {
    private DoctorSchedule schedule;
}