package com.camel.clinic.processor.faq;

import com.camel.clinic.dto.faq.CreateFaqDto;
import com.camel.clinic.service.faq.FaqServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("faqCreateProcessor")
@AllArgsConstructor
@Slf4j
public class FaqCreateProcessor implements Processor {
    private final FaqServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        CreateFaqDto request = exchange.getIn().getBody(CreateFaqDto.class);
        ResponseEntity<?> response = serviceImp.create(request);
        exchange.getIn().setBody(response);
    }
}