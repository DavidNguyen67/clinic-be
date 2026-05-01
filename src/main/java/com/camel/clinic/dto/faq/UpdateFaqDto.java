package com.camel.clinic.dto.faq;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateFaqDto {
    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    private String question;

    private String answer;

    @Min(value = 0, message = "Display order must be >= 0")
    private Integer displayOrder;

    private Boolean isActive;
}