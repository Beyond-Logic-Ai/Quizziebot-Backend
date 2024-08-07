package com.quizzka.backend.repository;

import com.quizzka.backend.entity.CustomQuizResult;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomQuizResultRepository extends MongoRepository<CustomQuizResult, String> {

}
