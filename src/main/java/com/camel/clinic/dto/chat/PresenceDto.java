package com.camel.clinic.dto.chat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PresenceDto {
    private String userId;
    private boolean isOnline;
}
