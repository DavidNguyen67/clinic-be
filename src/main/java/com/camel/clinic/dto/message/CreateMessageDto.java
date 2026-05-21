package com.camel.clinic.dto.message;

import com.camel.clinic.util.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMessageDto {

    @NotBlank(message = "Conversation ID is required")
    private String conversationId;

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Message type is required")
    private MessageType type; // TEXT, IMAGE, FILE

    private String replyTo; // nullable — messageId nếu trả lời
}