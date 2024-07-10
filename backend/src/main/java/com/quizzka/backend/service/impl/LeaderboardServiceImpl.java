package com.quizzka.backend.service.impl;

import com.quizzka.backend.entity.User;
import com.quizzka.backend.payload.response.LeaderboardEntry;
import com.quizzka.backend.repository.UserRepository;
import com.quizzka.backend.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class LeaderboardServiceImpl implements LeaderboardService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<LeaderboardEntry> getGlobalLeaderboard() {
        List<User> users = userRepository.findAllByOrderByXpDescScoreDesc();
        return getLeaderboardEntries(users);
    }

    @Override
    public List<LeaderboardEntry> getCountryLeaderboard(String country) {
        List<User> users = userRepository.findByCountryOrderByXpDescScoreDesc(country);
        return getLeaderboardEntries(users);
    }

    @Override
    public LeaderboardEntry getUserRank(String userId) {
        List<User> users = userRepository.findAllByOrderByXpDescScoreDesc();
        return getRankForUser(users, userId);
    }

    private List<LeaderboardEntry> getLeaderboardEntries(List<User> users) {
        return IntStream.range(0, users.size())
                .mapToObj(i -> {
                    User user = users.get(i);
                    return new LeaderboardEntry(user.getId(), user.getUsername(), user.getXp(), user.getScore(), i + 1, user.getLeague());
                })
                .limit(200)
                .collect(Collectors.toList());
    }

    private LeaderboardEntry getRankForUser(List<User> users, String userId) {
        return IntStream.range(0, users.size())
                .filter(i -> users.get(i).getId().equals(userId))
                .mapToObj(i -> {
                    User user = users.get(i);
                    return new LeaderboardEntry(user.getId(), user.getUsername(), user.getXp(), user.getScore(), i + 1, user.getLeague());
                })
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getCurrentLeague(int xp) {
        if (xp < 1000) {
            return "Bronze";
        } else if (xp < 5000) {
            return "Silver";
        } else if (xp < 10000) {
            return "Gold";
        } else {
            return "Platinum";
        }
    }
}
