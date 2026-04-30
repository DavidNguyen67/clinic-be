package com.camel.clinic.processor.auth;

import com.camel.clinic.service.CommonService;
import com.camel.clinic.service.auth.AuthServiceImp;
import com.camel.clinic.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("getUserProfile")
@AllArgsConstructor
@Slf4j
public class GetUserProfile implements Processor {
    private final AuthServiceImp authServiceImp;
    private final JwtUtil jwtUtil;
    private final CommonService commonService;

    @Override
    public void process(Exchange exchange) throws Exception {
        String accessToken = commonService.getAuthHeader(exchange);
        String userIdStr = jwtUtil.getUserIdFromToken(accessToken);
        UUID userId = commonService.parseUuid(userIdStr);

        ResponseEntity<?> response = authServiceImp.getUserProfile(userId);
        exchange.getIn().setBody(response);
    }
}

