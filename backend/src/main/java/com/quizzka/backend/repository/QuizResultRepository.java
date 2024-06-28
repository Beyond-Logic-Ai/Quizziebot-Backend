package com.quizzka.backend.repository;

import com.quizzka.backend.entity.QuizResult;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuizResultRepository extends MongoRepository<QuizResult, String> {
}
