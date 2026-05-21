package com.camel.clinic.processor.message;

import com.camel.clinic.service.message.MessageServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("messageCountProcessor")
@AllArgsConstructor
public class MessageCountProcessor implements Processor {
    private final MessageServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        ResponseEntity<?> response = serviceImp.count();
        exchange.getMessage().setBody(response);
    }
}
