package com.camel.clinic.dto.auth;

import com.camel.clinic.entity.Role;
import com.camel.clinic.entity.User;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @NotBlank(message = "Full name is required")
    @Size(max = 255, message = "Full name must not exceed 255 characters")
    private String name;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^(\\+84|0)[3|5|7|8|9][0-9]{8}$", message = "Invalid Vietnamese phone number")
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be 8–128 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password must contain uppercase, lowercase, digit, and special character"
    )
    private String password;

    @NotBlank(message = "Date of birth is required")
    @Pattern(
            regexp = "^\\d{2}/\\d{2}/\\d{4}$",
            message = "Date of birth must be in format DD/MM/YYYY"
    )
    private String dateOfBirth;

    @NotNull(message = "Gender is required")
    private User.Gender gender;

    @NotNull(message = "Role is required")
    private Role.RoleName role;

    private UUID specialtyId;

    @AssertTrue(message = "Specialty ID must be a valid UUID and required for doctors")
    public boolean isValidPromotion() {
        if (role == Role.RoleName.DOCTOR) {
            try {
                UUID.fromString(specialtyId.toString());
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return true;
    }
}