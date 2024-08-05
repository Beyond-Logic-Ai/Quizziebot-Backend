package com.quizzka.backend.service.impl;

import com.quizzka.backend.entity.UserPlayStats;
import com.quizzka.backend.repository.UserPlayStatsRepository;
import com.quizzka.backend.service.UserPlayStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserPlayStatsServiceImpl implements UserPlayStatsService {

    @Autowired
    private UserPlayStatsRepository userPlayStatsRepository;

    @Override
    public Optional<UserPlayStats> findByUserId(String userId) {
        return userPlayStatsRepository.findByUserId(userId);
    }
}
