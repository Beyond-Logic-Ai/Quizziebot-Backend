package com.quizzka.backend.repository;

import com.quizzka.backend.entity.ChatSession;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatSessionRepository extends MongoRepository<ChatSession, String> {
    List<ChatSession> findByUserIdAndEndedAtIsNull(String userId);
}