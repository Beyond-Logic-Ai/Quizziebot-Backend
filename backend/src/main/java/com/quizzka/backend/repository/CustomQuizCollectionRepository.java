package com.quizzka.backend.repository;

import com.quizzka.backend.entity.CustomQuizCollection;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomQuizCollectionRepository extends MongoRepository<CustomQuizCollection, String> {

}
