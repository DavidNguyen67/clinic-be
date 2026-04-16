package com.camel.clinic.processor.slotGenerator;

import com.camel.clinic.service.slotGenerator.SlotGeneratorServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component("getSlotGeneratorProcessor")
@AllArgsConstructor
public class GetSlotGeneratorProcessor implements Processor {
    private final SlotGeneratorServiceImp slotGeneratorServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        Map<String, Object> queryParams = exchange.getIn().getHeaders();

        ResponseEntity<?> response = slotGeneratorServiceImp.getAvailableSlots(queryParams);

        exchange.getMessage().setBody(response);
    }
}
