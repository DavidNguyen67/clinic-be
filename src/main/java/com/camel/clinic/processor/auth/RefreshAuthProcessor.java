package com.camel.clinic.processor.auth;

import com.camel.clinic.dto.auth.RefreshRequestDTO;
import com.camel.clinic.service.auth.AuthServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("refreshAuthProcessor")
@AllArgsConstructor
@Slf4j
public class RefreshAuthProcessor implements Processor {

    private final AuthServiceImp authServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        RefreshRequestDTO request = exchange.getIn().getBody(RefreshRequestDTO.class);

        ResponseEntity<?> response = authServiceImp.refresh(request);

        exchange.getIn().setBody(response);
    }
}

