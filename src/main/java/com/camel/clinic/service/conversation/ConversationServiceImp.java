package com.camel.clinic.service.conversation;

import com.camel.clinic.document.ConversationDocument;
import com.camel.clinic.dto.conversation.CreateConversationDto;
import com.camel.clinic.dto.conversation.ResponseConversationDto;
import com.camel.clinic.dto.conversation.UpdateConversationDto;
import com.camel.clinic.exception.BadRequestException;
import com.camel.clinic.util.ConversationType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class ConversationServiceImp implements ConversationService {

    private final ConversationServiceInv serviceInv;

    @Override
    public ResponseEntity<?> list(Map<String, Object> queryParams) {
        return serviceInv.list(queryParams);
    }

    @Override
    public ResponseEntity<?> count() {
        return serviceInv.count();
    }

    @Override
    public ResponseEntity<?> retrieve(String id) {
        return serviceInv.retrieve(id, null);
    }

    @Override
    public ResponseEntity<?> create(CreateConversationDto requestBody) {
        if (requestBody.getType() == ConversationType.DIRECT
                && requestBody.getParticipants().size() != 2) {
            throw new BadRequestException("DIRECT conversation must have exactly 2 participants.");
        }

        if (requestBody.getType() == ConversationType.GROUP
                && (requestBody.getName() == null || requestBody.getName().isBlank())) {
            throw new BadRequestException("GROUP conversation must have a name.");
        }

        ConversationDocument conversation = new ConversationDocument();
        conversation.setParticipants(requestBody.getParticipants());
        conversation.setType(requestBody.getType());
        conversation.setName(requestBody.getName());
        conversation.setAvatar(requestBody.getAvatar());

        return serviceInv.create(conversation);
    }

    @Override
    public ResponseEntity<?> update(String id, UpdateConversationDto requestBody) {
        ResponseConversationDto existing = serviceInv.retrieve(id, null).getBody()
                instanceof ResponseConversationDto c ? c : null;

        if (existing == null) {
            throw new BadRequestException("Conversation with ID " + id + " not found");
        }

        ConversationDocument patch = new ConversationDocument();

        patch.setParticipants(requestBody.getParticipants());
        patch.setName(requestBody.getName());
        patch.setAvatar(requestBody.getAvatar());
        patch.setLastMessage(requestBody.getLastMessage());

        ConversationDocument saved = (ConversationDocument) serviceInv.update(id, patch, null).getBody();
        assert saved != null;
        return ResponseEntity.ok(ResponseConversationDto.from(saved));
    }

    @Override
    public ResponseEntity<?> delete(String id) {
        return serviceInv.delete(id);
    }

    @Override
    public ResponseEntity<?> restore(String id) {
        return serviceInv.restore(id);
    }

}