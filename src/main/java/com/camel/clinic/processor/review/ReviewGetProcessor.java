package com.camel.clinic.processor.review;

import com.camel.clinic.service.review.ReviewServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("reviewGetProcessor")
@AllArgsConstructor
@Slf4j
public class ReviewGetProcessor implements Processor {
    private final ReviewServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) {
        String id = exchange.getIn().getHeader("id", String.class);

        String appointmentId = exchange.getIn().getHeader("appointmentId", String.class);
        ResponseEntity<?> response;

        if (id != null) {
            response = serviceImp.retrieve(id);
        } else {
            response = serviceImp.retrieveByAppointmentId(appointmentId);
        }

        exchange.getIn().setBody(response);
    }
}