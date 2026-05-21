package com.camel.clinic.processor.message;

import com.camel.clinic.dto.message.UpdateMessageDto;
import com.camel.clinic.service.message.MessageServiceImp;
import com.camel.clinic.util.JwtUtil;
import com.camel.clinic.util.SecuritiesUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("messageUpdateProcessor")
@AllArgsConstructor
@Slf4j
public class MessageUpdateProcessor implements Processor {
    private final MessageServiceImp serviceImp;
    private final JwtUtil jwtUtil;

    @Override
    public void process(Exchange exchange) throws Exception {
        UpdateMessageDto request = exchange.getIn().getBody(UpdateMessageDto.class);
        String id = exchange.getIn().getHeader("id", String.class);
        String accessToken = SecuritiesUtils.getAccessToken(exchange);
        String userId = jwtUtil.getUserIdFromToken(accessToken);
        
        ResponseEntity<?> response = serviceImp.update(id, request, userId);
        exchange.getIn().setBody(response);
    }
}