package com.quizzka.backend.repository;

import com.quizzka.backend.entity.CustomQuizSession;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CustomQuizSessionRepository extends MongoRepository<CustomQuizSession, String> {
    Optional<CustomQuizSession> findByQuizId(String quizId);
}

