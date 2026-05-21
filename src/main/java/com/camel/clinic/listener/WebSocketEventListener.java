package com.camel.clinic.listener;

import com.camel.clinic.dto.chat.PresenceDto;
import com.camel.clinic.service.presence.PresenceService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@EnableWebSocketMessageBroker
public class WebSocketEventListener {

    private final PresenceService presenceService;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketEventListener(PresenceService presenceService,
                                  SimpMessagingTemplate messagingTemplate) {
        this.presenceService = presenceService;
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleConnect(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        if (accessor.getUser() != null) {
            String userId = accessor.getUser().getName();
            presenceService.setOnline(userId);
            PresenceDto presenceDto = new PresenceDto();
            presenceDto.setUserId(userId);
            presenceDto.setOnline(true);

            messagingTemplate.convertAndSend("/topic/presence", presenceDto);
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        if (accessor.getUser() != null) {
            String userId = accessor.getUser().getName();
            presenceService.setOffline(userId);
            PresenceDto presenceDto = new PresenceDto();
            presenceDto.setUserId(userId);
            presenceDto.setOnline(false);

            messagingTemplate.convertAndSend("/topic/presence", presenceDto);
        }
    }
}