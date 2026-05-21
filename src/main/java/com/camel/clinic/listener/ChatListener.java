package com.camel.clinic.listener;

import com.camel.clinic.dto.chat.ReadReceiptDto;
import com.camel.clinic.dto.chat.TypingPayloadDto;
import com.camel.clinic.dto.message.CreateMessageDto;
import com.camel.clinic.dto.message.ResponseMessageDto;
import com.camel.clinic.service.message.MessageServiceImp;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@AllArgsConstructor
public class ChatListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageServiceImp messageServiceImp;

    @MessageMapping("/chat/{conversationId}")
    public void sendMessage(
            @DestinationVariable String conversationId,
            @Payload CreateMessageDto payload,
            Principal principal
    ) {
        ResponseMessageDto saved =
                messageServiceImp.create(payload, principal.getName()).getBody()
                        instanceof ResponseMessageDto msg ? msg : null;
        assert saved != null;
        messagingTemplate.convertAndSend(
                "/topic/conversation/" + conversationId,
                saved
        );
    }

    @MessageMapping("/typing/{conversationId}")
    public void typing(
            @DestinationVariable String conversationId,
            @Payload TypingPayloadDto payload,
            Principal principal
    ) {
        payload.setUserId(principal.getName());
        messagingTemplate.convertAndSend(
                "/topic/conversation/" + conversationId + "/typing",
                payload
        );
    }

    @MessageMapping("/read/{messageId}")
    public void markRead(
            @DestinationVariable String messageId,
            Principal principal
    ) {
        messageServiceImp.markAsRead(messageId, principal.getName());
        messagingTemplate.convertAndSend(
                "/topic/message/" + messageId + "/read",
                new ReadReceiptDto(messageId, principal.getName())
        );
    }
}