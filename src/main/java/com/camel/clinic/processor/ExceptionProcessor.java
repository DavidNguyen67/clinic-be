package com.camel.clinic.processor;

import com.camel.clinic.dto.api.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePropertyKey;
import org.apache.camel.Processor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class ExceptionProcessor implements Processor {


    public ExceptionProcessor(ObjectMapper objectMapper) {
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Exception cause = exchange.getProperty(ExchangePropertyKey.EXCEPTION_CAUGHT, Exception.class);

        int status;
        String code;

        if (cause instanceof BadRequestException) {
            status = 400;
            code = "BAD_REQUEST";
        } else if (cause instanceof AccessDeniedException) {
            status = 403;
            code = "FORBIDDEN";
        } else if (cause instanceof EntityNotFoundException) {
            status = 404;
            code = "NOT_FOUND";
        } else {
            status = 500;
            code = "INTERNAL_SERVER_ERROR";
        }

        exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, status);
        exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "application/json");
        exchange.getMessage().setBody(ApiResponse.error(code, cause.getMessage()));
    }
}