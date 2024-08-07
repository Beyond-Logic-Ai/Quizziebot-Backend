package com.quizzka.backend.service;

import com.quizzka.backend.entity.ChatSession;

public interface ChatService {
    ChatSession startSession(String userId);
    ChatSession endSession(String sessionId);
    ChatSession addMessage(String sessionId, String userId, String message) throws Exception;
}
