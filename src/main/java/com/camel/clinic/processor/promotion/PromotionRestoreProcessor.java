package com.camel.clinic.processor.promotion;

import com.camel.clinic.service.promotion.PromotionServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("promotionRestoreProcessor")
@AllArgsConstructor
public class PromotionRestoreProcessor implements Processor {
    private final PromotionServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        String id = exchange.getIn().getHeader("id", String.class);
        ResponseEntity<?> response = serviceImp.restore(id);
        exchange.getMessage().setBody(response);
    }
}
