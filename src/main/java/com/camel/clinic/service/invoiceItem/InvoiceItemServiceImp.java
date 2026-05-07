package com.camel.clinic.service.invoiceItem;

import com.camel.clinic.dto.invoiceItem.CreateInvoiceItemDto;
import com.camel.clinic.dto.invoiceItem.UpdateInvoiceItemDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class InvoiceItemServiceImp implements InvoiceItemService {
    private final InvoiceItemServiceInv serviceInv;

    @Override
    public ResponseEntity<?> count() {
        return serviceInv.count();
    }

    @Override
    public ResponseEntity<?> retrieve(String id) {
        return serviceInv.retrieve(id, null);
    }

    @Override
    public ResponseEntity<?> create(CreateInvoiceItemDto requestBody) {
        return null;
    }

    @Override
    public ResponseEntity<?> update(String id, UpdateInvoiceItemDto requestBody) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> delete(String id) {
        return serviceInv.delete(id);
    }

    @Override
    public ResponseEntity<?> restore(String id) {
        return serviceInv.restore(id);
    }

    @Override
    public ResponseEntity<?> list(Map<String, Object> queryParams) {
        return serviceInv.list(queryParams);
    }
}
