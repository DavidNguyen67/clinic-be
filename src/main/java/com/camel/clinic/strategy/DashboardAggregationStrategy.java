package com.camel.clinic.strategy;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("dashboardAggregationStrategy")
public class DashboardAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            Map<String, Object> result = new HashMap<>();
            mergeBody(result, newExchange);
            newExchange.getIn().setBody(result);
            return newExchange;
        }

        Map<String, Object> result = oldExchange.getIn()
            .getBody(Map.class);
        mergeBody(result, newExchange);
        oldExchange.getIn().setBody(result);
        return oldExchange;
    }

    private void mergeBody(Map<String, Object> result, Exchange exchange) {
        Object body = exchange.getIn().getBody();
        if (body instanceof Map) {
            result.putAll((Map<String, Object>) body);
        }
    }
}