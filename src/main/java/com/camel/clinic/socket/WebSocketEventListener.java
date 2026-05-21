package com.camel.clinic.socket;

import com.camel.clinic.dto.chat.PresenceDto;
import com.camel.clinic.service.presence.PresenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
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
        log.info("New WebSocket connection established");
        log.info("Headers: {}", event.getUser());
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
        log.info("WebSocket connection closed");
        log.info("Headers: {}", event.getUser());
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