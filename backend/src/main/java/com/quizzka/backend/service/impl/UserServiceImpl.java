package com.quizzka.backend.service.impl;

import com.quizzka.backend.entity.User;
import com.quizzka.backend.payload.response.HomeScreenResponse;
import com.quizzka.backend.repository.UserRepository;
import com.quizzka.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User findUserById(String userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public HomeScreenResponse getHomeScreenData(String identifier) {
        User user = userRepository.findByEmail(identifier)
                .orElseGet(() -> userRepository.findByPhoneNumber(identifier)
                        .orElseGet(() -> userRepository.findByUsername(identifier)
                                .orElseThrow(() -> new RuntimeException("User not found with identifier: " + identifier))));

        HomeScreenResponse response = new HomeScreenResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setProfilePictureUrl(user.getProfilePictureUrl());
        response.setXp(user.getXp());
        response.setCoins(user.getCoins());
        response.setCountry(user.getCountry());
        response.setLastLogin(user.getLastLogin());

        return response;
    }

    @Override
    public Optional<User> findById(String userId) {
        return userRepository.findById(userId);
    }

    @Override
    public void updateLastLoginTime(String identifier) {
        User user = userRepository.findByEmail(identifier)
                .orElseGet(() -> userRepository.findByPhoneNumber(identifier)
                        .orElseGet(() -> userRepository.findByUsername(identifier)
                                .orElseThrow(() -> new RuntimeException("User not found with identifier: " + identifier))));

        user.setLastLogin(new Date());
        userRepository.save(user);
    }
    @Override
    public List<User> findAllByOrderByXpDescScoreDesc() {
        return userRepository.findAllByOrderByXpDescScoreDesc();
    }
}
