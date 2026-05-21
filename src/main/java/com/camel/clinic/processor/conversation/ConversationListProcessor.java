package com.camel.clinic.processor.conversation;

import com.camel.clinic.service.conversation.ConversationServiceImp;
import com.camel.clinic.util.JwtUtil;
import com.camel.clinic.util.SecuritiesUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("conversationListProcessor")
@AllArgsConstructor
@Slf4j
public class ConversationListProcessor implements Processor {
    private final ConversationServiceImp serviceImp;
    private final JwtUtil jwtUtil;

    @Override
    public void process(Exchange exchange) {
        Map<String, Object> queryParams = exchange.getIn().getHeaders();
        String accessToken = SecuritiesUtils.getAccessToken(exchange);
        String userId = jwtUtil.getUserIdFromToken(accessToken);

        queryParams.put("userId", userId);
        
        ResponseEntity<?> response = serviceImp.list(queryParams);

        exchange.getMessage().setBody(response);
    }
}