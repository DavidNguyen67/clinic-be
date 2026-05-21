package com.camel.clinic.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReadReceiptDto {
    private String messageId;
    private String userId;
}