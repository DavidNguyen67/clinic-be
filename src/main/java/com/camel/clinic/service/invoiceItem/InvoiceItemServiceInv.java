package com.camel.clinic.service.invoiceItem;

import com.camel.clinic.entity.InvoiceItem;
import com.camel.clinic.repository.InvoiceItemRepository;
import com.camel.clinic.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InvoiceItemServiceInv extends BaseService<InvoiceItem, InvoiceItemRepository> {

    public InvoiceItemServiceInv(InvoiceItemRepository repository) {
        super(InvoiceItem::new, repository);
    }

}