package com.camel.clinic.processor.promotion;

import com.camel.clinic.dto.promotion.UpdatePromotionDto;
import com.camel.clinic.service.promotion.PromotionServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("promotionUpdateProcessor")
@AllArgsConstructor
@Slf4j
public class PromotionUpdateProcessor implements Processor {
    private final PromotionServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        UpdatePromotionDto request = exchange.getIn().getBody(UpdatePromotionDto.class);
        String id = exchange.getIn().getHeader("id", String.class);

        ResponseEntity<?> response = serviceImp.update(id, request);
        exchange.getIn().setBody(response);
    }
}