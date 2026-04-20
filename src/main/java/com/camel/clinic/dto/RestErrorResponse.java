package com.camel.clinic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestErrorResponse {
    private Map<String, Object> headers = new HashMap<>();
    private Object body;
    private String statusCode;
    private Integer statusCodeValue;
}