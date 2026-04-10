package com.camel.clinic.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("getOrdersProcessor")
public class GetOrdersProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.getIn().setBody(Map.of(
            "orders", List.of(
                Map.of("orderId", "001", "amount", 150000),
                Map.of("orderId", "002", "amount", 300000)
            )
        ));
    }
}