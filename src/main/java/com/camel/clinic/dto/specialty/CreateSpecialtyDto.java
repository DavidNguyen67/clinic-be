package com.camel.clinic.dto.specialty;

import com.camel.clinic.entity.Specialty;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSpecialtyDto {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Slug is required")
    @Size(max = 120, message = "Slug must not exceed 120 characters")
    @Pattern(
            regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
            message = "Slug must be lowercase alphanumeric with hyphens only (e.g. 'nhi-khoa')"
    )
    private String slug;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String image;

    @Min(value = 0, message = "Display order must be 0 or greater")
    private Integer displayOrder = 0;

    private Boolean isActive = true;

    @NotNull(message = "Specialty type is required")
    private Specialty.SpecialtyType specialtyType;
}