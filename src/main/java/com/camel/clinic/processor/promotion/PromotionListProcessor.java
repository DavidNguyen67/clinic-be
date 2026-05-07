package com.camel.clinic.processor.promotion;

import com.camel.clinic.service.promotion.PromotionServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("promotionListProcessor")
@AllArgsConstructor
@Slf4j
public class PromotionListProcessor implements Processor {
    private final PromotionServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) {
        Map<String, Object> queryParams = exchange.getIn().getHeaders();

        ResponseEntity<?> response = serviceImp.list(queryParams);

        exchange.getMessage().setBody(response);
    }
}