// CreateInvoiceItemDto.java
package com.camel.clinic.dto.invoiceItem;

import com.camel.clinic.entity.InvoiceItem.ItemType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class CreateInvoiceItemDto {

    @NotNull(message = "Item type is required")
    private ItemType itemType;

    @NotBlank(message = "Item name is required")
    @Size(max = 255, message = "Item name must not exceed 255 characters")
    private String itemName;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity = 1;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Unit price format is invalid")
    private BigDecimal unitPrice;
}