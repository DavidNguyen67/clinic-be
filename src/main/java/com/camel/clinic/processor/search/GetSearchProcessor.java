package com.camel.clinic.processor.search;

import com.camel.clinic.service.search.SearchServiceImp;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component("getSearchProcessor")
@RequiredArgsConstructor
public class GetSearchProcessor implements Processor {

    private final SearchServiceImp searchServiceImp;

    @Override
    public void process(Exchange exchange) {
        String query = exchange.getIn().getHeader("q", String.class);
        String type = exchange.getIn().getHeader("type", String.class);
        exchange.getMessage().setBody(searchServiceImp.search(query, type));
    }
}

