package com.camel.clinic.service.message;

import com.camel.clinic.document.MessageDocument;
import com.camel.clinic.repository.MessageRepository;
import com.camel.clinic.service.BaseMongoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageServiceInv extends BaseMongoService<MessageDocument, MessageRepository> {
    public final MongoTemplate mongoTemplate;

    public MessageServiceInv(MessageRepository repository, MongoTemplate mongoTemplate) {
        super(MessageDocument::new, repository, mongoTemplate, MessageDocument.class);
        this.mongoTemplate = mongoTemplate;
    }
}