package com.quizzka.backend.service;

import com.quizzka.backend.entity.User;
import com.quizzka.backend.payload.response.HomeScreenResponse;

import java.util.List;

public interface UserService {
    User findUserById(String userId);
    HomeScreenResponse getHomeScreenData(String identifier);
    void updateLastLoginTime(String identifier);
}
