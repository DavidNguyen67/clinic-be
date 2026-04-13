package com.camel.clinic.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    @NotBlank()
    private String email;
    @NotBlank()
    private String password;
}

