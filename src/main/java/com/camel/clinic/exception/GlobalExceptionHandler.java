package com.camel.clinic.exception;

import com.camel.clinic.util.ErrorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import com.camel.clinic.dto.Error;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add(String.format("\"%s\": %s", fieldName, errorMessage));
        });

        return ErrorUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), String.join(", ", errors));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Error> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return ErrorUtils.createErrorResponse(HttpStatus.METHOD_NOT_ALLOWED.value(), ex.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Error> handleNoResourceFound(NoResourceFoundException ex) {
        return ErrorUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Error> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return ErrorUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Error> handleResponseStatusException(ResponseStatusException ex) {
        String message = ex.getReason() != null ? ex.getReason() : ex.getMessage();
        return ErrorUtils.createErrorResponse(ex.getStatusCode().value(), message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleInternalServerError(Exception ex) {
        log.error("Unhandled exception", ex);
        String message = ex.getMessage() != null ? ex.getMessage() : "Internal Server Error";
        return ErrorUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
    }

    @ExceptionHandler(ResponseEntityException.class)
    public ResponseEntity<Error> handleResponseEntityException(ResponseEntityException ex) {
        log.error("ResponseEntityException: {}", ex.getMessage());
        int statusCode = ex.getStatus() != null ? ex.getStatus().value() : HttpStatus.INTERNAL_SERVER_ERROR.value();
        String message = ex.getMessage() != null ? ex.getMessage() : "Internal Server Error";
        return ErrorUtils.createErrorResponse(statusCode, message);
    }
}
