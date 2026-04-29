package com.camel.clinic.processor.user;

import com.camel.clinic.service.user.UserServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("userGetProcessor")
@AllArgsConstructor
@Slf4j
public class UserGetProcessor implements Processor {
    private final UserServiceImp userServiceImp;

    @Override
    public void process(Exchange exchange) {
        String id = exchange.getIn().getHeader("id", String.class);
        ResponseEntity<?> response = userServiceImp.retrieve(id);
        exchange.getIn().setBody(response);
    }
}