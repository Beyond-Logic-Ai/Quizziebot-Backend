package com.quizzka.backend.service;

import com.quizzka.backend.entity.User;

import java.util.List;

public interface UserService {
    User findUserById(String userId);
}
