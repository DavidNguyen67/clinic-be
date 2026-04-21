package com.camel.clinic.dto.patient;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePatientProfileDto {

    @NotBlank()
    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^(\\+84|0)[3|5|7|8|9][0-9]{8}$", message = "Invalid Vietnamese phone number")
    private String phone;


    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    private String address;

    @Pattern(
            regexp = "^[A-Z0-9]{10,15}$",
            message = "Số bảo hiểm không hợp lệ (10-15 ký tự, chỉ gồm chữ in hoa và số)"
    )
    private String insuranceNumber;

    @Size(max = 500, message = "Thông tin dị ứng không được vượt quá 500 ký tự")
    private String allergies;

    @Size(max = 500, message = "Thông tin bệnh mãn tính không được vượt quá 500 ký tự")
    private String chronicDiseases;
}
