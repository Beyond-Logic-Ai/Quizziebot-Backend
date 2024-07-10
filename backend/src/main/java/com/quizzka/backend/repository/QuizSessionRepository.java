package com.quizzka.backend.repository;

import com.quizzka.backend.entity.QuizSession;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface QuizSessionRepository extends MongoRepository<QuizSession, String> {
    List<QuizSession> findByUserId(String userId);
    Optional<QuizSession> findByQuizId(String quizId);
}
