package com.camel.clinic.dto.slotGenerator;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class SlotGeneratorRequestDTO {
    @NotBlank()
    private UUID doctorId;
    @NotBlank()
    private LocalDate date;
}
