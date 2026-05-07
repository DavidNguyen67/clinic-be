// CreateInvoiceDto.java
package com.camel.clinic.dto.invoice;

import com.camel.clinic.dto.invoiceItem.CreateInvoiceItemDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateInvoiceDto {
    private String appointmentId;

    @NotNull(message = "Invoice date is required")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date invoiceDate;

    private List<CreateInvoiceItemDto> items;
}