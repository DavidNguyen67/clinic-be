package com.camel.clinic.util;

import com.camel.clinic.dto.api.ApiResponse;
import com.camel.clinic.dto.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ErrorUtils {
    /**
     * New global error response format (preferred):
     * { "success": false, "error": { "code", "message", "field" } }
     */
    public static ResponseEntity<ApiResponse<Void>> createApiErrorResponse(int statusCode, String code, String message, String field) {
        ApiResponse<Void> body = ApiResponse.error(code, message, field);
        return new ResponseEntity<>(body, HttpStatus.valueOf(statusCode));
    }

    public static ResponseEntity<ApiResponse<Void>> createApiErrorResponse(int statusCode, String code, String message) {
        return createApiErrorResponse(statusCode, code, message, null);
    }

    public static String defaultErrorCode(int statusCode) {
        return "ERR" + statusCode;
    }

    public static ResponseEntity<Error> createErrorResponse(int statusCode, String message) {
        Error error = new Error();
        error.setAtType("Error");
        error.setReason(HttpStatus.valueOf(statusCode).getReasonPhrase());
        error.setCode("ERR" + statusCode);
        error.setMessage(message);

        return new ResponseEntity<>(error, HttpStatus.valueOf(statusCode));
    }

    public static ResponseEntity<Error> createErrorResponse(Error error) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        try {
            if (error.getStatus() != null) {
                httpStatus = HttpStatus.valueOf(Integer.parseInt(error.getStatus()));
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            // fallback về 500 nếu lỗi hoặc status không hợp lệ
        }

        return new ResponseEntity<>(error, httpStatus);
    }
}
