package com.camel.clinic.processor.user;

import com.camel.clinic.service.user.UserServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("userListProcessor")
@AllArgsConstructor
@Slf4j
public class UserListProcessor implements Processor {
    private final UserServiceImp userServiceImp;

    @Override
    public void process(Exchange exchange) {
        Map<String, Object> queryParams = exchange.getIn().getHeaders();

        ResponseEntity<?> response = userServiceImp.list(queryParams);

        exchange.getMessage().setBody(response);
    }
}