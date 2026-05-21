package com.camel.clinic.repository;

import com.camel.clinic.document.ConversationDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepository extends MongoRepository<ConversationDocument, String> {
}