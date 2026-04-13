package com.camel.clinic.config;

import com.camel.clinic.exception.BadRequestException;
import com.camel.clinic.exception.NotFoundException;
import com.camel.clinic.exception.UnauthorizedException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CamelExceptionHandlerConfig extends RouteBuilder {

    @Override
    public void configure() {

        onException(UnauthorizedException.class)
                .handled(true)
                .process(buildErrorProcessor(401));

        onException(BadRequestException.class)
                .handled(true)
                .process(buildErrorProcessor(400));

        onException(NotFoundException.class)
                .handled(true)
                .process(buildErrorProcessor(404));

        onException(Exception.class)          // fallback
                .handled(true)
                .process(buildErrorProcessor(500));
    }

    private Processor buildErrorProcessor(int statusCode) {
        return exchange -> {
            Exception ex = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, statusCode);
            exchange.getIn().setHeader("Content-Type", "application/json");
            exchange.getIn().setBody(Map.of(
                    "status", statusCode,
                    "message", ex.getMessage() != null ? ex.getMessage() : "Unexpected error"
            ));
        };
    }
}