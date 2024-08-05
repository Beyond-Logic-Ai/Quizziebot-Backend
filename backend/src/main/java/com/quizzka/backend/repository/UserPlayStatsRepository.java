package com.quizzka.backend.repository;

import com.quizzka.backend.entity.UserPlayStats;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserPlayStatsRepository extends MongoRepository<UserPlayStats, String> {
    Optional<UserPlayStats> findByUserId(String userId);
}
