package com.camel.clinic.processor.auth;

import com.camel.clinic.service.user.UserServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("countEmailAuthProcessor")
@AllArgsConstructor
@Slf4j
public class CountEmailAuthProcessor implements Processor {
    private final UserServiceImp serviceImp;

    @Override
    public void process(org.apache.camel.Exchange exchange) throws Exception {
        Map<String, Object> queryParams = exchange.getIn().getHeaders();

        exchange.getMessage().setBody(serviceImp.countWithSpec(queryParams));

    }
}
