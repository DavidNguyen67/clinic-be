package com.camel.clinic.document;

import com.camel.clinic.util.MessageStatus;
import com.camel.clinic.util.MessageType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "messages")
@CompoundIndexes({
        @CompoundIndex(name = "idx_conv_created", def = "{'conversation_id': 1, 'created_at': 1}"),
        @CompoundIndex(name = "idx_conv_status", def = "{'conversation_id': 1, 'status': 1}")
})
@Getter
@Setter
public class MessageDocument extends SofDeleteDocument {

    @Field("conversation_id")
    private String conversationId;

    @Field("sender_id")
    private String senderId; // userId từ PostgreSQL

    @Field("content")
    private String content;

    @Field("type")
    private MessageType type; // TEXT, IMAGE, FILE

    @Field("status")
    private MessageStatus status; // SENT, DELIVERED, READ

    @Field("read_by")
    private List<String> readBy = new ArrayList<>();

    @Field("reply_to")
    private String replyTo; // messageId nếu trả lời
}