package com.camel.clinic.exception;

import com.camel.clinic.util.ErrorUtils;
import com.camel.clinic.dto.api.ApiResponse;
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

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // FE expects a single {code,message,field}. If multiple errors, pick the first.
        List<FieldError> fieldErrors = new ArrayList<>(ex.getBindingResult().getFieldErrors());
        String field = null;
        String message = "Validation error";
        if (!fieldErrors.isEmpty()) {
            FieldError fe = fieldErrors.get(0);
            field = fe.getField();
            message = fe.getDefaultMessage() != null ? fe.getDefaultMessage() : message;
        }
        return ErrorUtils.createApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                message,
                field
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return ErrorUtils.createApiErrorResponse(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                ErrorUtils.defaultErrorCode(HttpStatus.METHOD_NOT_ALLOWED.value()),
                ex.getMessage()
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFound(NoResourceFoundException ex) {
        return ErrorUtils.createApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ErrorUtils.defaultErrorCode(HttpStatus.NOT_FOUND.value()),
                ex.getMessage()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return ErrorUtils.createApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "BAD_REQUEST",
                ex.getMessage()
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatusException(ResponseStatusException ex) {
        String message = ex.getReason() != null ? ex.getReason() : ex.getMessage();
        int status = ex.getStatusCode().value();
        return ErrorUtils.createApiErrorResponse(status, ErrorUtils.defaultErrorCode(status), message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleInternalServerError(Exception ex) {
        log.error("Unhandled exception", ex);
        String message = ex.getMessage() != null ? ex.getMessage() : "Internal Server Error";
        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        return ErrorUtils.createApiErrorResponse(status, ErrorUtils.defaultErrorCode(status), message);
    }

    @ExceptionHandler(ResponseEntityException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseEntityException(ResponseEntityException ex) {
        log.error("ResponseEntityException: {}", ex.getMessage());
        int statusCode = ex.getStatus() != null ? ex.getStatus().value() : HttpStatus.INTERNAL_SERVER_ERROR.value();
        String message = ex.getMessage() != null ? ex.getMessage() : "Internal Server Error";
        return ErrorUtils.createApiErrorResponse(statusCode, ErrorUtils.defaultErrorCode(statusCode), message);
    }
}
