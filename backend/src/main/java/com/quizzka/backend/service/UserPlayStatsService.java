package com.quizzka.backend.service;

import com.quizzka.backend.entity.UserPlayStats;

import java.util.Optional;

public interface UserPlayStatsService {
    Optional<UserPlayStats> findByUserId(String userId);
}
