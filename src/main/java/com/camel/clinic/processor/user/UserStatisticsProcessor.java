package com.camel.clinic.processor.user;

import com.camel.clinic.service.user.UserServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("userStatisticsProcessor")
@AllArgsConstructor
@Slf4j
public class UserStatisticsProcessor implements Processor {
    private final UserServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) {
        ResponseEntity<?> response = serviceImp.calculateStatistics();

        exchange.getMessage().setBody(response);
    }
}
