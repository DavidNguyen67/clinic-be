package com.camel.clinic.exception;

import com.camel.clinic.processor.ExceptionProcessor;
import lombok.AllArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GlobalExceptionHandler extends RouteBuilder {
    private final ExceptionProcessor exceptionProcessor;

    @Override
    public void configure() {
        onException(Exception.class)
                .handled(true)
                .log(">>> GlobalException caught: ${exception.message}")
                .process(exceptionProcessor)
                .marshal().json();
    }
}