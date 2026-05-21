package com.camel.clinic.document;

import com.camel.clinic.util.ConversationType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Document(collection = "conversations")
@CompoundIndexes({
        @CompoundIndex(name = "idx_participants", def = "{'participants': 1}"),
        @CompoundIndex(name = "idx_updated_at", def = "{'updated_at': -1}")
})
@Getter
@Setter
public class ConversationDocument extends SofDeleteDocument {

    @Field("participants")
    private List<String> participants; // userId (từ PostgreSQL)

    @Field("type")
    private ConversationType type; // DIRECT, GROUP

    @Field("name")
    private String name; // group chat only

    @Field("avatar")
    private String avatar;

    @Field("last_message")
    private LastMessageSnapshot lastMessage; // denormalized để hiển thị nhanh

    @Getter
    @Setter
    public static class LastMessageSnapshot {
        @Field("sender_id")
        private String senderId;
        @Field("content")
        private String content;
        @Field("sent_at")
        private Date sentAt;
    }
}