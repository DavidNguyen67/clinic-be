package com.camel.clinic.processor.auth;

import com.camel.clinic.dto.auth.ChangePasswordRequestDTO;
import com.camel.clinic.service.auth.AuthServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("changePasswordAuthProcessor")
@AllArgsConstructor
@Slf4j
public class ChangePasswordAuthProcessor implements Processor {
    private final AuthServiceImp authServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        ChangePasswordRequestDTO request = exchange.getIn().getBody(ChangePasswordRequestDTO.class);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;

        ResponseEntity<?> response = authServiceImp.changePassword(request, email);
        exchange.getIn().setBody(response);
    }
}

