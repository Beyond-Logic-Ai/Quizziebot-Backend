package com.quizzka.backend.repository;

import com.quizzka.backend.entity.QuizQuestion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface QuizQuestionRepository extends MongoRepository<QuizQuestion, String> {
    List<QuizQuestion> findTop15ByTopic(String topic);
}
