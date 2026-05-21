package com.camel.clinic.processor.message;

import com.camel.clinic.dto.message.CreateMessageDto;
import com.camel.clinic.service.message.MessageServiceImp;
import com.camel.clinic.util.JwtUtil;
import com.camel.clinic.util.SecuritiesUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("messageCreateProcessor")
@AllArgsConstructor
@Slf4j
public class MessageCreateProcessor implements Processor {
    private final MessageServiceImp serviceImp;
    private final JwtUtil jwtUtil;

    @Override
    public void process(Exchange exchange) throws Exception {
        CreateMessageDto request = exchange.getIn().getBody(CreateMessageDto.class);
        String accessToken = SecuritiesUtils.getAccessToken(exchange);
        String userId = jwtUtil.getUserIdFromToken(accessToken);

        ResponseEntity<?> response = serviceImp.create(request, userId);
        exchange.getIn().setBody(response);
    }
}