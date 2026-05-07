package com.camel.clinic.processor.promotion;

import com.camel.clinic.service.promotion.PromotionServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("promotionCountProcessor")
@AllArgsConstructor
public class PromotionCountProcessor implements Processor {
    private final PromotionServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        ResponseEntity<?> response = serviceImp.count();
        exchange.getMessage().setBody(response);
    }
}
