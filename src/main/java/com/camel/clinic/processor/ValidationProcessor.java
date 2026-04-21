package com.camel.clinic.processor;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component("validationProcessor")
public class ValidationProcessor implements Processor {
    private final Validator validator;

    public ValidationProcessor(Validator validator) {
        this.validator = validator;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Object body = exchange.getIn().getBody();
        if (body == null) return;

        Set<ConstraintViolation<Object>> violations = validator.validate(body);
        if (!violations.isEmpty()) {
            ConstraintViolation<Object> first = violations.iterator().next();
            throw new BadRequestException(first.getMessage());
        }
    }
}