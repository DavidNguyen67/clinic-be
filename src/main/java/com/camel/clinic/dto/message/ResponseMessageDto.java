package com.camel.clinic.dto.message;

import com.camel.clinic.document.MessageDocument;
import com.camel.clinic.util.MessageStatus;
import com.camel.clinic.util.MessageType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
public class ResponseMessageDto {

    private String id;

    private String conversationId;

    private String senderId;

    private String content;

    private MessageType type;

    private MessageStatus status;

    private List<String> readBy;

    private String replyTo;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "HH:mm:ss dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date createdAt;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "HH:mm:ss dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date updatedAt;

    private boolean deleted;

    public static ResponseMessageDto from(MessageDocument document) {
        return ResponseMessageDto.builder()
                .id(document.getId())
                .conversationId(document.getConversationId())
                .senderId(document.getSenderId())
                .content(document.getContent())
                .type(document.getType())
                .status(document.getStatus())
                .readBy(document.getReadBy())
                .replyTo(document.getReplyTo())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .deleted(document.isDeleted())
                .build();
    }
}