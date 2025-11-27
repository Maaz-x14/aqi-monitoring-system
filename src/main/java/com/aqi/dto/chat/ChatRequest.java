package com.aqi.dto.chat;

import lombok.Data;
import java.util.List;

@Data
public class ChatRequest {
    private String message;
    private List<MessageContext> history; // <-- New Field

    @Data
    public static class MessageContext {
        private String role; // "user" or "assistant"
        private String content;
    }
}