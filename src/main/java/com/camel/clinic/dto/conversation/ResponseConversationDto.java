package com.camel.clinic.dto.conversation;

import com.camel.clinic.document.ConversationDocument;
import com.camel.clinic.util.ConversationType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ResponseConversationDto {

    private String id;
    private List<String> participants;
    private ConversationType type;
    private String name;
    private String avatar;
    private LastMessageSnapshotDto lastMessage;
    private Date createdAt;
    private Date updatedAt;
    private boolean deleted;

    public static ResponseConversationDto from(ConversationDocument doc) {
        if (doc == null) return null;
        ResponseConversationDto dto = new ResponseConversationDto();
        dto.setId(doc.getId());
        dto.setParticipants(doc.getParticipants());
        dto.setType(doc.getType());
        dto.setName(doc.getName());
        dto.setAvatar(doc.getAvatar());
        dto.setLastMessage(LastMessageSnapshotDto.from(doc.getLastMessage()));
        dto.setCreatedAt(doc.getCreatedAt());
        dto.setUpdatedAt(doc.getUpdatedAt());
        dto.setDeleted(doc.isDeleted());
        return dto;
    }

    @Getter
    @Setter
    public static class LastMessageSnapshotDto {
        private String senderId;
        private String content;
        private Date sentAt;

        public static LastMessageSnapshotDto from(ConversationDocument.LastMessageSnapshot snapshot) {
            if (snapshot == null) return null;
            LastMessageSnapshotDto dto = new LastMessageSnapshotDto();
            dto.setSenderId(snapshot.getSenderId());
            dto.setContent(snapshot.getContent());
            dto.setSentAt(snapshot.getSentAt());
            return dto;
        }
    }
}