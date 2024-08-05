package com.quizzka.backend.service;

import com.quizzka.backend.entity.User;
import com.quizzka.backend.payload.response.HomeScreenResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> findById(String userId);
    User findUserById(String userId);
    HomeScreenResponse getHomeScreenData(String identifier);
    void updateLastLoginTime(String identifier);
    List<User> findAllByOrderByXpDescScoreDesc();
}
