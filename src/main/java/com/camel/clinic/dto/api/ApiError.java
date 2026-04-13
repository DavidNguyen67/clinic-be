package com.camel.clinic.dto.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;

    /**
     * field gây lỗi (validation). Null nếu không phải validation error.
     */
    @JsonProperty("field")
    private String field;

    public ApiError() {
    }

    public ApiError(String code, String message, String field) {
        this.code = code;
        this.message = message;
        this.field = field;
    }

    public static ApiError of(String code, String message) {
        return new ApiError(code, message, null);
    }

    public static ApiError of(String code, String message, String field) {
        return new ApiError(code, message, field);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}

