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

    public UserPlayStats getOrCreateUserPlayStats(String userId, String username) {
        return userPlayStatsRepository.findByUserId(userId).orElseGet(() -> {
            UserPlayStats newUserPlayStats = new UserPlayStats();
            newUserPlayStats.setUserId(userId);
            newUserPlayStats.setUserName(username);
            newUserPlayStats.setClassicPlays(0);
            newUserPlayStats.setArcadePlays(0);
            newUserPlayStats.setTotalTimeSpent(0);
            newUserPlayStats.setOverallIq(0);
            newUserPlayStats.setStreak(0);
            newUserPlayStats.setTopPositions(0);
            newUserPlayStats.setChallengePassed(0);
            newUserPlayStats.setFastestRecord(0);
            return userPlayStatsRepository.save(newUserPlayStats);
        });
    }
}
