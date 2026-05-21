package com.camel.clinic.service.message;

import com.camel.clinic.document.ConversationDocument;
import com.camel.clinic.document.MessageDocument;
import com.camel.clinic.dto.message.CreateMessageDto;
import com.camel.clinic.dto.message.ResponseMessageDto;
import com.camel.clinic.dto.message.UpdateMessageDto;
import com.camel.clinic.exception.BadRequestException;
import com.camel.clinic.service.conversation.ConversationServiceInv;
import com.camel.clinic.util.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class MessageServiceImp implements MessageService {

    private final MessageServiceInv serviceInv;
    private final ConversationServiceInv conversationServiceInv;
    private final ProducerTemplate producerTemplate;

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
    public ResponseEntity<?> create(CreateMessageDto requestBody, String senderId) {
        MessageDocument message = new MessageDocument();
        message.setConversationId(requestBody.getConversationId());
        message.setSenderId(senderId);
        message.setContent(requestBody.getContent());
        message.setType(requestBody.getType());
        message.setStatus(MessageStatus.SENT);
        message.setReplyTo(requestBody.getReplyTo());

        MessageDocument saved = (MessageDocument) serviceInv.create(message).getBody();

        ConversationDocument conversation = conversationServiceInv.findOne(
                Map.of("userId", senderId, "conversationId", requestBody.getConversationId())
        );

        if (conversation != null) {
            ConversationDocument.LastMessageSnapshot lastMsg = new ConversationDocument.LastMessageSnapshot();
            lastMsg.setSenderId(senderId);
            lastMsg.setContent(requestBody.getContent());
            assert saved != null;
            lastMsg.setSentAt(saved.getCreatedAt());

            ConversationDocument patch = new ConversationDocument();
            patch.setLastMessage(lastMsg);

            conversationServiceInv.update(conversation.getId(), patch, null);
        }
        //TODO:Async publish sang RabbitMQ qua Camel (cho notification/email về sau)
//        producerTemplate.asyncSendBody(
//                "spring-rabbitmq:chat.exchange?routingKey=chat.message.new",
//                saved
//        );
        assert saved != null;
        return ResponseEntity.ok(ResponseMessageDto.from(saved));
    }

    @Override
    public void markAsRead(String messageId, String userId) {
        serviceInv.markAsRead(messageId, userId);
    }

    @Override
    public ResponseEntity<?> update(String id, UpdateMessageDto requestBody, String senderId) {
        Object body = serviceInv.retrieve(id, null).getBody();
        if (!(body instanceof ResponseMessageDto existing)) {
            throw new BadRequestException("Message with ID " + id + " not found");
        }

        if (requestBody.getContent() != null
                && !existing.getSenderId().equals(senderId)) {
            throw new BadRequestException("You are not allowed to edit this message");
        }

        MessageDocument patch = new MessageDocument();
        patch.setContent(requestBody.getContent());
        patch.setStatus(requestBody.getStatus());

        MessageDocument saved = (MessageDocument) serviceInv.update(id, patch, null).getBody();
        assert saved != null;
        return ResponseEntity.ok(ResponseMessageDto.from(saved));
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