package com.camel.clinic.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.List;

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
        Boolean isList = exchange.getIn().getHeader("X-DTO-List", Boolean.class);

        if (targetType != null && body != null) {
            if (Boolean.TRUE.equals(isList)) {
                var listType = objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, targetType);
                exchange.getIn().setBody(objectMapper.readValue(body, listType));
            } else {
                exchange.getIn().setBody(objectMapper.readValue(body, targetType));
            }
        }
    }
}