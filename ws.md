# WebSocket Topics & DTOs

## Topics (Server → Client)

| Topic                                         | Trigger                   | Direction                 |
|-----------------------------------------------|---------------------------|---------------------------|
| `/topic/presence`                             | User connect / disconnect | Broadcast                 |
| `/topic/conversation/{conversationId}`        | New message sent          | Broadcast to conversation |
| `/topic/conversation/{conversationId}/typing` | User typing               | Broadcast to conversation |
| `/topic/message/{messageId}/read`             | Message marked as read    | Broadcast                 |

---

## Message Mappings (Client → Server)

| Destination                    | Payload DTO        | Mô tả           |
|--------------------------------|--------------------|-----------------|
| `/app/chat/{conversationId}`   | `CreateMessageDto` | Gửi tin nhắn    |
| `/app/typing/{conversationId}` | `TypingPayloadDto` | Báo đang gõ     |
| `/app/read/{messageId}`        | *(no body)*        | Đánh dấu đã đọc |

---

## DTOs

### `PresenceDto`

> Publish lên `/topic/presence`

```java
String userId;
boolean isOnline;
```

---

### `CreateMessageDto`

> Client gửi lên `/app/chat/{conversationId}`

```java
// nội dung tạo tin nhắn (conversationId, content, v.v.)
```

---

### `ResponseMessageDto`

> Server push xuống `/topic/conversation/{conversationId}`

```java
// dữ liệu tin nhắn đã lưu (id, senderId, content, createdAt, v.v.)
```

---

### `TypingPayloadDto`

> Client gửi lên `/app/typing/{conversationId}`, server broadcast lại

```java
String userId;  // được set lại từ principal (bỏ qua userId client tự khai)
// + các field khác nếu có
```

---

### `ReadReceiptDto`

> Publish lên `/topic/message/{messageId}/read`

```java
String messageId;
String userId;
```

---