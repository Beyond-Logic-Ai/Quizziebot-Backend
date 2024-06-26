package com.quizzka.backend.repository;

import com.quizzka.backend.entity.UserResponse;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserResponseRepository extends MongoRepository<UserResponse, String> {
    Optional<UserResponse> findTopByUserIdOrderByQuizEndTimeDesc(String userId);
}
