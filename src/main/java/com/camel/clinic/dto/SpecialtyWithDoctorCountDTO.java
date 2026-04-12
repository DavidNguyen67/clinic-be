package com.camel.clinic.dto;

import com.camel.clinic.entity.Specialty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpecialtyWithDoctorCountDTO {
    private UUID id;
    private String name;
    private String slug;
    private String description;
    private String image;
    private Integer displayOrder;
    private Boolean isActive;
    private Specialty.SpecialtyType specialtyType;
    private Long doctorCount;
}