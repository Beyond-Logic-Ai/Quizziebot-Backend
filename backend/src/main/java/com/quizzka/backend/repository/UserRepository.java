package com.quizzka.backend.repository;

import com.quizzka.backend.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findTop10ByLeagueOrderByTotalXpDesc(String league);
    List<User> findByLeagueOrderByTotalXpDesc(String league);
    Optional<User> findByResetToken(String resetToken);
    boolean existsByUsername(String username);
    User findByUsername(String username);
}
