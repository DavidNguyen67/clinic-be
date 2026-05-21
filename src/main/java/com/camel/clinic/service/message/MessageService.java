package com.camel.clinic.service.message;

import com.camel.clinic.dto.message.CreateMessageDto;
import com.camel.clinic.dto.message.UpdateMessageDto;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface MessageService {

    ResponseEntity<?> list(Map<String, Object> queryParams);

    ResponseEntity<?> count();

    ResponseEntity<?> retrieve(String id);

    ResponseEntity<?> create(CreateMessageDto requestBody, String senderId);

    void markAsRead(String messageId, String userId);

    ResponseEntity<?> update(String id, UpdateMessageDto requestBody, String senderId);

    ResponseEntity<?> delete(String id);

    ResponseEntity<?> restore(String id);
}