package com.camel.clinic.processor.faq;

import com.camel.clinic.service.faq.FaqServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("faqCountProcessor")
@AllArgsConstructor
public class FaqCountProcessor implements Processor {
    private final FaqServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        ResponseEntity<?> response = serviceImp.count();
        exchange.getMessage().setBody(response);
    }
}
