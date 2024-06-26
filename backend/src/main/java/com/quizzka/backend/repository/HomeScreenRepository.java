package com.quizzka.backend.repository;

import com.quizzka.backend.entity.HomeScreen;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeScreenRepository extends MongoRepository<HomeScreen, String> {
}
