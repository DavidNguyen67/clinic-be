package com.camel.clinic.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("getProfileProcessor")
public class GetProfileProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String userId = exchange.getIn().getHeader("userId", String.class);
        // Gọi service thực tế ở đây
        exchange.getIn().setBody(Map.of(
            "profile", Map.of(
                "userId", userId,
                "name", "Nguyen Van A",
                "email", "a@gmail.com"
            )
        ));
    }
}



