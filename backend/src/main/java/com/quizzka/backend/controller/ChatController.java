package com.quizzka.backend.controller;

import com.quizzka.backend.entity.ChatSession;
import com.quizzka.backend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/startSession")
    public ResponseEntity<ChatSession> startSession(@RequestParam String userId) {
        ChatSession session = chatService.startSession(userId);
        return ResponseEntity.ok(session);
    }

    @PostMapping("/sendMessage")
    public ResponseEntity<ChatSession> sendMessage(@RequestParam String sessionId, @RequestParam String userId, @RequestBody String message) {
        try {
            ChatSession session = chatService.addMessage(sessionId, userId, message);
            return ResponseEntity.ok(session);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/endSession")
    public ResponseEntity<ChatSession> endSession(@RequestParam String sessionId) {
        ChatSession session = chatService.endSession(sessionId);
        return ResponseEntity.ok(session);
    }
}
