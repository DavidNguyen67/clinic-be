package com.camel.clinic.dto.message;

import com.camel.clinic.util.MessageStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMessageDto {
    private String content;

    private MessageStatus status;
}