package com.camel.clinic.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatusCode;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseEntityException extends RuntimeException {
    private HttpStatusCode status;

    public ResponseEntityException(String message, HttpStatusCode status) {
        super(message);
        this.status = status;
    }
}
