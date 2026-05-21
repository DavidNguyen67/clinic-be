package com.camel.clinic.processor.conversation;

import com.camel.clinic.dto.conversation.CreateConversationDto;
import com.camel.clinic.service.conversation.ConversationServiceImp;
import com.camel.clinic.util.JwtUtil;
import com.camel.clinic.util.SecuritiesUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component("conversationCreateProcessor")
@AllArgsConstructor
@Slf4j
public class ConversationCreateProcessor implements Processor {
    private final ConversationServiceImp serviceImp;
    private final JwtUtil jwtUtil;

    @Override
    public void process(Exchange exchange) throws Exception {
        String accessToken = SecuritiesUtils.getAccessToken(exchange);
        String userId = jwtUtil.getUserIdFromToken(accessToken);

        CreateConversationDto request = exchange.getIn().getBody(CreateConversationDto.class);

        List<String> participants = Stream.concat(
                        Stream.of(userId),
                        request.getParticipants().stream()
                )
                .distinct()
                .collect(Collectors.toList());

        request.setParticipants(participants);

        exchange.getIn().setBody(serviceImp.create(request));
    }
}