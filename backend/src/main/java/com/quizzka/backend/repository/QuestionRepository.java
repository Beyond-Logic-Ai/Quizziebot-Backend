package com.quizzka.backend.repository;

import com.quizzka.backend.entity.Question;
import com.quizzka.backend.entity.QuestionCollection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends MongoRepository<QuestionCollection, String> {
    List<QuestionCollection> findByCategory(String category);
}
