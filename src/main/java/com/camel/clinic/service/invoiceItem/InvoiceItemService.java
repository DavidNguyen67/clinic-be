package com.camel.clinic.service.invoiceItem;

import com.camel.clinic.dto.invoiceItem.CreateInvoiceItemDto;
import com.camel.clinic.dto.invoiceItem.UpdateInvoiceItemDto;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface InvoiceItemService {
    ResponseEntity<?> list(Map<String, Object> queryParams);

    ResponseEntity<?> count();

    ResponseEntity<?> retrieve(String id);

    ResponseEntity<?> create(CreateInvoiceItemDto requestBody);

    ResponseEntity<?> update(String id, UpdateInvoiceItemDto requestBody);

    ResponseEntity<?> delete(String id);

    ResponseEntity<?> restore(String id);
}
