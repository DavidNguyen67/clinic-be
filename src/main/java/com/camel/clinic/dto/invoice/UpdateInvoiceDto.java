// UpdateInvoiceDto.java
package com.camel.clinic.dto.invoice;

import com.camel.clinic.dto.invoiceItem.UpdateInvoiceItemDto;
import com.camel.clinic.entity.Invoice.InvoiceStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UpdateInvoiceDto {

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date invoiceDate;

    @DecimalMin(value = "0.0", message = "Discount amount must be >= 0")
    @Digits(integer = 8, fraction = 2, message = "Discount amount format is invalid")
    private BigDecimal discountAmount;

    @DecimalMin(value = "0.0", message = "Insurance covered must be >= 0")
    @Digits(integer = 8, fraction = 2, message = "Insurance covered format is invalid")
    private BigDecimal insuranceCovered;

    @DecimalMin(value = "0.0", message = "Patient paid must be >= 0")
    @Digits(integer = 8, fraction = 2, message = "Patient paid format is invalid")
    private BigDecimal patientPaid;

    private InvoiceStatus status;

    private List<UpdateInvoiceItemDto> items = List.of();
}