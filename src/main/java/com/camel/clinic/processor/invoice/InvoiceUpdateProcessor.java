package com.camel.clinic.processor.invoice;

import com.camel.clinic.dto.invoice.UpdateInvoiceDto;
import com.camel.clinic.service.invoice.InvoiceServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("invoiceUpdateProcessor")
@AllArgsConstructor
@Slf4j
public class InvoiceUpdateProcessor implements Processor {
    private final InvoiceServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        UpdateInvoiceDto request = exchange.getIn().getBody(UpdateInvoiceDto.class);
        String id = exchange.getIn().getHeader("id", String.class);

        ResponseEntity<?> response = serviceImp.update(id, request, true);
        exchange.getIn().setBody(response);
    }
}