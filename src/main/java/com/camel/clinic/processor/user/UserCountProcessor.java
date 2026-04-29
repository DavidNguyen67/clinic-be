package com.camel.clinic.processor.user;

import com.camel.clinic.service.user.UserServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("userCountProcessor")
@AllArgsConstructor
public class UserCountProcessor implements Processor {
    private final UserServiceImp userServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        ResponseEntity<?> response = userServiceImp.count();
        exchange.getMessage().setBody(response);
    }
}
