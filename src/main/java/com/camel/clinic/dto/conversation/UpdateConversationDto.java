package com.camel.clinic.dto.conversation;

import com.camel.clinic.document.ConversationDocument.LastMessageSnapshot;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateConversationDto {

    // Cho phép thêm/bớt thành viên (GROUP)
    private List<String> participants;

    // Đổi tên nhóm (GROUP only)
    private String name;

    // Đổi avatar nhóm (GROUP only)
    private String avatar;

    // Cập nhật snapshot tin nhắn cuối — thường do MessageService gọi nội bộ
    private LastMessageSnapshot lastMessage;
}