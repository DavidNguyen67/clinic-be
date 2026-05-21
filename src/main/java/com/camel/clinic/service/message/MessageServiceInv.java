package com.camel.clinic.service.message;

import com.camel.clinic.document.MessageDocument;
import com.camel.clinic.repository.MessageRepository;
import com.camel.clinic.service.BaseMongoService;
import com.camel.clinic.util.MessageStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageServiceInv extends BaseMongoService<MessageDocument, MessageRepository> {
    public final MongoTemplate mongoTemplate;

    public MessageServiceInv(MessageRepository repository, MongoTemplate mongoTemplate) {
        super(MessageDocument::new, repository, mongoTemplate, MessageDocument.class);
        this.mongoTemplate = mongoTemplate;
    }

    public void markAsRead(String messageId, String userId) {
        mongoTemplate.updateFirst(
                Query.query(
                        Criteria.where("_id").is(messageId)
                ),
                new Update()
                        .addToSet("read_by", userId)
                        .set("status", MessageStatus.READ),
                MessageDocument.class
        );
    }
}