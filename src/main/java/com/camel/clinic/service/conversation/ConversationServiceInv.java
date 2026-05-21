package com.camel.clinic.service.conversation;

import com.camel.clinic.document.ConversationDocument;
import com.camel.clinic.repository.ConversationRepository;
import com.camel.clinic.service.BaseMongoService;
import com.camel.clinic.util.ConversationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ConversationServiceInv extends BaseMongoService<ConversationDocument, ConversationRepository> {
    protected final MongoTemplate mongoTemplate;

    public ConversationServiceInv(ConversationRepository repository, MongoTemplate mongoTemplate) {
        super(ConversationDocument::new, repository, mongoTemplate, ConversationDocument.class);
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    protected Criteria buildCriteria(Map<String, Object> queryParams) {
        String userId = (String) queryParams.get("userId");
        String conversationId = (String) queryParams.get("conversationId");
        String keyword = (String) queryParams.get("keyword");
        List<ConversationType> type =
                parseEnumList(queryParams.get("type"), ConversationType.class);

        return andAll(
                notDeleted(),
                multiFieldIn(userId != null ? List.of(userId) : List.of(), new String[]{"participants"}),
                multiFieldIn(type, new String[]{"type"}),
                multiFieldLike(keyword, new String[]{"name"}),
                multiFieldEquals(conversationId, new String[]{"_id"})
        );
    }

    public ConversationDocument findOne(Map<String, Object> queryParams) {
        return mongoTemplate.findOne(Query.query(buildCriteria(queryParams)), ConversationDocument.class);
    }
}