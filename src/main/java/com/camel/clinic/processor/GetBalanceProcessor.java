package com.camel.clinic.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("getBalanceProcessor")
public class GetBalanceProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.getIn().setBody(Map.of(
            "balance", Map.of(
                "available", 500000,
                "currency", "VND"
            )
        ));
    }
}