package com.quizzka.backend.service.impl;

import com.quizzka.backend.entity.ChatMessage;
import com.quizzka.backend.entity.ChatSession;
import com.quizzka.backend.repository.ChatSessionRepository;
import com.quizzka.backend.service.AIService;
import com.quizzka.backend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private AIService aiService;

    @Override
    public ChatSession startSession(String userId) {
        ChatSession session = new ChatSession();
        session.setUserId(userId);
        session.setStartedAt(new Date());
        session.setMessages(new ArrayList<>());
        chatSessionRepository.save(session);
        return session;
    }

    @Override
    public ChatSession addMessage(String sessionId, String userId, String message) throws Exception {
        Optional<ChatSession> optionalSession = chatSessionRepository.findById(sessionId);
        if (optionalSession.isPresent()) {
            ChatSession session = optionalSession.get();
            if (!session.getUserId().equals(userId)) {
                throw new RuntimeException("User does not own this session");
            }

            ChatMessage userMessage = new ChatMessage("user", message, new Date());
            session.getMessages().add(userMessage);

            String historyPrompt = buildHistoryPrompt(session.getMessages());
            String aiResponse = aiService.fetchResponseFromAI(historyPrompt + "\nUser: " + message);

            ChatMessage geminiMessage = new ChatMessage("gemini", aiResponse, new Date());
            session.getMessages().add(geminiMessage);

            // Check for quizzable topic in AI response
            String quizzableTopic = extractQuizzableTopic(aiResponse);
            if (quizzableTopic != null) {
                session.setIdentifiedTopic(quizzableTopic);
            }

            chatSessionRepository.save(session);

            return session;
        } else {
            throw new RuntimeException("Session not found");
        }
    }

    @Override
    public ChatSession endSession(String sessionId) {
        Optional<ChatSession> optionalSession = chatSessionRepository.findById(sessionId);
        if (optionalSession.isPresent()) {
            ChatSession session = optionalSession.get();
            session.setEndedAt(new Date());
            chatSessionRepository.save(session);
            return session;
        } else {
            throw new RuntimeException("Session not found");
        }
    }

    private String buildHistoryPrompt(List<ChatMessage> messages) {
        StringBuilder prompt = new StringBuilder("You are chatting with a user. Respond to their messages as naturally as possible.\n\n" +
                "If the user requests information about a topic, provide a brief overview of the topic.\n" +
                "If the user asks directly for a quiz on a topic, include 'Topic:' followed by the topic name in your response without asking any quiz questions or providing further information.\n" +
                "Always include 'Topic:' followed by the topic name in your response if a quizzable topic is mentioned.\n\n");
        for (ChatMessage message : messages) {
            prompt.append(message.getSender()).append(": ").append(message.getMessage()).append("\n");
        }
        prompt.append("gemini: ");
        return prompt.toString();
    }



    private String extractQuizzableTopic(String aiResponse) {
        String topicKeyword = "Topic:";
        int topicIndex = aiResponse.indexOf(topicKeyword);
        if (topicIndex != -1) {
            return aiResponse.substring(topicIndex + topicKeyword.length()).trim();
        }
        return null;
    }
}