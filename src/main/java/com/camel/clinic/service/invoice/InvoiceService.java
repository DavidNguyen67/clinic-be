package com.camel.clinic.service.invoice;

import com.camel.clinic.dto.invoice.CreateInvoiceDto;
import com.camel.clinic.dto.invoice.UpdateInvoiceDto;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface InvoiceService {
    ResponseEntity<?> list(Map<String, Object> queryParams);

    ResponseEntity<?> count();

    ResponseEntity<?> retrieve(String id);

    ResponseEntity<?> create(CreateInvoiceDto requestBody);

    ResponseEntity<?> update(String id, UpdateInvoiceDto requestBody, boolean isFromProcessor);

    ResponseEntity<?> delete(String id);

    ResponseEntity<?> restore(String id);
}
