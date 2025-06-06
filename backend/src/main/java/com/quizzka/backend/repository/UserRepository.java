package com.quizzka.backend.repository;

import com.quizzka.backend.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByResetToken(String resetToken);
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    List<User> findByCountryOrderByXpDescScoreDesc(String country);
    List<User> findAllByOrderByXpDescScoreDesc();
}
