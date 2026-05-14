package com.camel.clinic.dto.auth;

import com.camel.clinic.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileDto {
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @Pattern(
            regexp = "^\\d{2}/\\d{2}/\\d{4}$",
            message = "Date of birth must be in format DD/MM/YYYY"
    )
    private String dateOfBirth;

    @Pattern(regexp = "^(\\+84|0)[3|5|7|8|9][0-9]{8}$", message = "Invalid Vietnamese phone number")
    private String phoneNumber;

    @Size(max = 255, message = "Full name must not exceed 255 characters")
    private String fullName;

    @Size(max = 500, message = "Avatar path must not exceed 500 characters")
    private String pathAvatar;

    private User.Gender gender;
}
