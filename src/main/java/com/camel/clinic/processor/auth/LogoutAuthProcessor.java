package com.camel.clinic.processor.auth;

import com.camel.clinic.dto.auth.LogoutRequestDTO;
import com.camel.clinic.service.CommonService;
import com.camel.clinic.service.auth.AuthServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("logoutAuthProcessor")
@AllArgsConstructor
@Slf4j
public class LogoutAuthProcessor implements Processor {
    private final AuthServiceImp authServiceImp;
    private final CommonService commonService;

    @Override
    public void process(Exchange exchange) throws Exception {
        LogoutRequestDTO request = exchange.getIn().getBody(LogoutRequestDTO.class);

        String refreshToken = request.getRefreshToken();
        String accessToken = commonService.getAuthHeader(exchange);

        ResponseEntity<?> response = authServiceImp.logout(refreshToken, accessToken);
        exchange.getIn().setBody(response);
    }

}

