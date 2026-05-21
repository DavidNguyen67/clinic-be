package com.camel.clinic.dto.chat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TypingPayloadDto {
    private String userId;
    private boolean typing;
}