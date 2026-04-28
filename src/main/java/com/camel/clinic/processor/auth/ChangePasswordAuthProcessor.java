package com.camel.clinic.processor.auth;

import com.camel.clinic.dto.auth.ChangePasswordRequestDTO;
import com.camel.clinic.service.CommonService;
import com.camel.clinic.service.auth.AuthServiceImp;
import com.camel.clinic.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("changePasswordAuthProcessor")
@AllArgsConstructor
@Slf4j
public class ChangePasswordAuthProcessor implements Processor {
    private final AuthServiceImp authServiceImp;
    private final JwtUtil jwtUtil;
    private final CommonService commonService;

    @Override
    public void process(Exchange exchange) throws Exception {
        ChangePasswordRequestDTO request = exchange.getIn().getBody(ChangePasswordRequestDTO.class);
        String accessToken = commonService.getAuthHeader(exchange);
        String email = jwtUtil.getEmailFromToken(accessToken);

        ResponseEntity<?> response = authServiceImp.changePassword(request, email);
        exchange.getIn().setBody(response);
    }
}

