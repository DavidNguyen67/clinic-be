package com.camel.clinic.service.conversation;

import com.camel.clinic.dto.conversation.CreateConversationDto;
import com.camel.clinic.dto.conversation.UpdateConversationDto;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface ConversationService {

    ResponseEntity<?> list(Map<String, Object> queryParams);

    ResponseEntity<?> count();

    ResponseEntity<?> retrieve(String id);

    ResponseEntity<?> create(CreateConversationDto requestBody);

    ResponseEntity<?> update(String id, UpdateConversationDto requestBody);

    ResponseEntity<?> delete(String id);

    ResponseEntity<?> restore(String id);
}