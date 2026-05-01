package com.camel.clinic.processor.faq;

import com.camel.clinic.service.faq.FaqServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("faqRestoreProcessor")
@AllArgsConstructor
public class FaqRestoreProcessor implements Processor {
    private final FaqServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        String id = exchange.getIn().getHeader("id", String.class);
        ResponseEntity<?> response = serviceImp.restore(id);
        exchange.getMessage().setBody(response);
    }
}
