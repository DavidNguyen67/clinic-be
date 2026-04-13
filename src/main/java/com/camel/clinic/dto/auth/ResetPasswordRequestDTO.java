package com.camel.clinic.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequestDTO {
    @NotBlank
    private String resetToken;

    @NotBlank
    @Size(min = 6, max = 100)
    private String newPassword;
}

