package com.camel.clinic.config;

import com.camel.clinic.processor.ExceptionProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CamelExceptionHandlerConfig extends RouteBuilder {

    private final ExceptionProcessor exceptionProcessor;

    public CamelExceptionHandlerConfig(ExceptionProcessor exceptionProcessor) {
        this.exceptionProcessor = exceptionProcessor;
    }

    @Override
    public void configure() {

        onException(Exception.class)
                .handled(true)
                .process(exceptionProcessor)
                .marshal().json();
    }
}