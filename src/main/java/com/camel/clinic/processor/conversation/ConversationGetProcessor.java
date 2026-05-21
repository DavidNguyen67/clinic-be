package com.camel.clinic.processor.conversation;

import com.camel.clinic.service.conversation.ConversationServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("conversationGetProcessor")
@AllArgsConstructor
@Slf4j
public class ConversationGetProcessor implements Processor {
    private final ConversationServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) {
        String id = exchange.getIn().getHeader("id", String.class);
        ResponseEntity<?> response = serviceImp.retrieve(id);
        exchange.getIn().setBody(response);
    }
}