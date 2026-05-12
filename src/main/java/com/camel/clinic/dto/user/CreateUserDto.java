package com.camel.clinic.dto.user;

import com.camel.clinic.entity.Role;
import com.camel.clinic.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CreateUserDto {

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
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date dateOfBirth;

    @NotNull(message = "Gender is required")
    private User.Gender gender;

    @NotNull(message = "Role is required")
    private Role.RoleName role;

    private String pathAvatar;
}