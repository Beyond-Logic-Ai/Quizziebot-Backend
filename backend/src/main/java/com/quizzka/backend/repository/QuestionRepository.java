package com.quizzka.backend.repository;

import com.quizzka.backend.entity.Question;
import com.quizzka.backend.entity.QuestionCollection;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface QuestionRepository extends MongoRepository<QuestionCollection, String> {
    List<QuestionCollection> findByCategory(String category);
}
