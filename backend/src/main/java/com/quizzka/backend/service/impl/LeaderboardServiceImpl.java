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
        updatePreviousRanks(users); // Update previous ranks
        return getLeaderboardEntries(users);
    }

    @Override
    public List<LeaderboardEntry> getCountryLeaderboard(String country) {
        List<User> users = userRepository.findByCountryOrderByXpDescScoreDesc(country);
        updatePreviousRanks(users); // Update previous ranks
        return getLeaderboardEntries(users);
    }

    @Override
    public LeaderboardEntry getUserRank(String userId) {
        List<User> users = userRepository.findAllByOrderByXpDescScoreDesc();
        updatePreviousRanks(users); // Update previous ranks
        return getRankForUser(users, userId);
    }

    @Override
    public LeaderboardEntry getPreviousRank(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            return new LeaderboardEntry(user.getId(), user.getUsername(), user.getXp(), user.getScore(), user.getPreviousRank(), user.getLeague());
        }
        return null;
    }

    private void updatePreviousRanks(List<User> users) {
        IntStream.range(0, users.size())
                .forEach(i -> {
                    User user = users.get(i);
                    user.setPreviousRank(i + 1); // Update previous rank
                    userRepository.save(user); // Save the user with the updated previous rank
                });
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
            return "BronzeStar";
        } else if (xp < 5000) {
            return "SilverStar";
        } else if (xp < 10000) {
            return "BronzeShield";
        } else if (xp < 20000) {
            return "GoldWings";
        } else {
            return "PlatinumWings";
        }
    }
}