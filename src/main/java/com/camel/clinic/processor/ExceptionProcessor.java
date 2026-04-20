package com.camel.clinic.processor;

import com.camel.clinic.dto.RestErrorResponse;
import com.camel.clinic.exception.BadRequestException;
import com.camel.clinic.exception.NotFoundException;
import com.camel.clinic.exception.UnauthorizedException;
import jakarta.persistence.EntityNotFoundException;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePropertyKey;
import org.apache.camel.Processor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class ExceptionProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        Exception cause = exchange.getProperty(ExchangePropertyKey.EXCEPTION_CAUGHT, Exception.class);

        int status;
        String code;

        if (cause instanceof BadRequestException || cause instanceof org.apache.coyote.BadRequestException) {
            status = 400;
            code = "BAD_REQUEST";
        } else if (cause instanceof UnauthorizedException) {
            status = 401;
            code = "UNAUTHORIZED";
        } else if (cause instanceof AccessDeniedException) {
            status = 403;
            code = "FORBIDDEN";
        } else if (cause instanceof EntityNotFoundException || cause instanceof NotFoundException) {
            status = 404;
            code = "NOT_FOUND";
        } else {
            status = 500;
            code = "INTERNAL_SERVER_ERROR";
        }

        RestErrorResponse errorResponse = new RestErrorResponse();
        errorResponse.setStatusCode(code);
        errorResponse.setStatusCodeValue(status);
        errorResponse.setBody(cause.getMessage());

        exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, status);
        exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "application/json");
        exchange.getMessage().setBody(errorResponse);
    }
}