package com.camel.clinic.dto.specialty;

import com.camel.clinic.entity.Specialty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class SpecialtyWithDoctorCountDto extends Specialty {
    private UUID id;

    private String name;

    private String slug;

    private String description;

    private String image;

    private SpecialtyType specialtyType;

    private long doctorCount;
}