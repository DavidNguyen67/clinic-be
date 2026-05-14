package com.camel.clinic.processor.auth;

import com.camel.clinic.dto.auth.UpdateProfileDto;
import com.camel.clinic.service.auth.AuthServiceImp;
import com.camel.clinic.util.JwtUtil;
import com.camel.clinic.util.SecuritiesUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("updateProfileAuthProcessor")
@AllArgsConstructor
@Slf4j
public class UpdateProfileAuthProcessor implements Processor {
    private final AuthServiceImp authServiceImp;
    private final JwtUtil jwtUtil;

    @Override
    public void process(Exchange exchange) throws Exception {
        UpdateProfileDto request = exchange.getIn().getBody(UpdateProfileDto.class);

        String accessToken = SecuritiesUtils.getAccessToken(exchange);
        String userIdStr = jwtUtil.getUserIdFromToken(accessToken);

        ResponseEntity<?> response = authServiceImp.updateProfile(userIdStr, request);
        exchange.getIn().setBody(response);
    }
}