package com.camel.clinic.processor.promotion;

import com.camel.clinic.dto.promotion.CreatePromotionDto;
import com.camel.clinic.service.promotion.PromotionServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("promotionCreateProcessor")
@AllArgsConstructor
@Slf4j
public class PromotionCreateProcessor implements Processor {
    private final PromotionServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        CreatePromotionDto request = exchange.getIn().getBody(CreatePromotionDto.class);
        ResponseEntity<?> response = serviceImp.create(request);
        exchange.getIn().setBody(response);
    }
}