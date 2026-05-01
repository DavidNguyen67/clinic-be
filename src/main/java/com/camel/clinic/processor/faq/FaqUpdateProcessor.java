package com.camel.clinic.processor.faq;

import com.camel.clinic.dto.faq.UpdateFaqDto;
import com.camel.clinic.service.faq.FaqServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("faqUpdateProcessor")
@AllArgsConstructor
@Slf4j
public class FaqUpdateProcessor implements Processor {
    private final FaqServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        UpdateFaqDto request = exchange.getIn().getBody(UpdateFaqDto.class);
        String id = exchange.getIn().getHeader("id", String.class);

        ResponseEntity<?> response = serviceImp.update(id, request);
        exchange.getIn().setBody(response);
    }
}