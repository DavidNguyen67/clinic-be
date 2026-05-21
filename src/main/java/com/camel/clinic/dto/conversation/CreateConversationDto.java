package com.camel.clinic.dto.conversation;

import com.camel.clinic.util.ConversationType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateConversationDto {

    @NotNull(message = "Participants must not be null")
    @NotEmpty(message = "Participants must not be empty")
    private List<String> participants;

    @NotNull(message = "Type must not be null")
    private ConversationType type;

    // Group chat only — optional
    private String name;

    private String avatar;
}