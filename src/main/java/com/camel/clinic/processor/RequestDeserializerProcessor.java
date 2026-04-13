package com.camel.clinic.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component("requestDeserializerProcessor")
public class RequestDeserializerProcessor implements Processor {
    private final ObjectMapper objectMapper;

    public RequestDeserializerProcessor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String body = exchange.getIn().getBody(String.class);
        Class<?> targetType = exchange.getIn().getHeader("X-DTO-Class", Class.class);
        if (targetType != null && body != null) {
            exchange.getIn().setBody(objectMapper.readValue(body, targetType));
        }
    }
}