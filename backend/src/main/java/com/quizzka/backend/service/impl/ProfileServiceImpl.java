package com.quizzka.backend.service.impl;

import com.quizzka.backend.entity.QuizResult;
import com.quizzka.backend.entity.User;
import com.quizzka.backend.entity.UserPlayStats;
import com.quizzka.backend.payload.request.ProfileUpdateRequest;
import com.quizzka.backend.payload.response.LeaderboardEntry;
import com.quizzka.backend.payload.response.ProfileResponse;
import com.quizzka.backend.payload.response.UserProfileResponse;
import com.quizzka.backend.repository.UserRepository;
import com.quizzka.backend.service.ProfileService;
import com.quizzka.backend.service.QuizResultService;
import com.quizzka.backend.service.UserPlayStatsService;
import com.quizzka.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserPlayStatsService userPlayStatsService;

    @Autowired
    private QuizResultService quizResultService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ProfileResponse getUserProfile(String userId) {
        Optional<User> userOptional = userService.findById(userId);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();
        UserPlayStats userPlayStats = userPlayStatsService.getOrCreateUserPlayStats(userId, user.getUsername());

        List<QuizResult> quizResults = quizResultService.findByUserId(userId);

        ProfileResponse response = new ProfileResponse();
        response.setUsername(user.getUsername());
        response.setFirstname(user.getFirstname());
        response.setLastname(user.getLastname());
        response.setEmail(user.getEmail());
        response.setDob(user.getDob().toLocalDate());
        response.setClassicGamesPlayed(userPlayStats.getClassicPlays());
        response.setTotalPlays(userPlayStats.getClassicPlays() + userPlayStats.getArcadePlays());
        response.setTotalTimeSpent(userPlayStats.getTotalTimeSpent());
        response.setTotalXp(user.getXp());
        response.setCoins(user.getCoins());
        response.setOverallIq(userPlayStats.getOverallIq());

        // Fetch and set rank
        LeaderboardEntry rankEntry = getUserRank(userId);
        response.setRank(rankEntry.getRank());

        List<ProfileResponse.IQDataPoint> iqGraph = quizResults.stream()
                .map(result -> {
                    ProfileResponse.IQDataPoint point = new ProfileResponse.IQDataPoint();
                    point.setDate(result.getCreatedAt().toString());
                    point.setIq(result.getIqScore());
                    return point;
                })
                .collect(Collectors.toList());
        response.setIqGraph(iqGraph);

        ProfileResponse.Achievements achievements = new ProfileResponse.Achievements();
        achievements.setStreak(userPlayStats.getStreak());

        int quizPassed = (int) quizResults.stream()
                .filter(result -> result.getCorrectAnswers() >= 4)
                .count();
        achievements.setQuizPassed(quizPassed);

        achievements.setTopPositions(userPlayStats.getTopPositions());
        achievements.setChallengePassed(userPlayStats.getChallengePassed());
        achievements.setFastestRecord(userPlayStats.getFastestRecord());
        response.setAchievements(achievements);

        return response;
    }

    public LeaderboardEntry getUserRank(String userId) {
        List<User> users = userService.findAllByOrderByXpDescScoreDesc();
        return getRankForUser(users, userId);
    }

    private LeaderboardEntry getRankForUser(List<User> users, String userId) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(userId)) {
                LeaderboardEntry entry = new LeaderboardEntry();
                entry.setRank(i + 1);
                entry.setUserId(userId);
                entry.setUsername(users.get(i).getUsername());
                entry.setXp(users.get(i).getXp());
                entry.setScore(users.get(i).getScore());
                return entry;
            }
        }
        throw new RuntimeException("User not found in the leaderboard");
    }

    @Override
    public UserProfileResponse updateProfile(ProfileUpdateRequest profileUpdateRequest) {
        User user = userRepository.findById(profileUpdateRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        profileUpdateRequest.getUsername().ifPresent(username -> {
            if (!user.getUsername().equals(username)) {
                if (userRepository.existsByUsername(username)) {
                    throw new RuntimeException("Username is already in use");
                }
                user.setUsername(username);
            }
        });

        profileUpdateRequest.getFirstname().ifPresent(user::setFirstname);
        profileUpdateRequest.getLastname().ifPresent(user::setLastname);
        profileUpdateRequest.getEmail().ifPresent(user::setEmail);
        profileUpdateRequest.getDob().ifPresent(dob -> user.setDob(dob.atStartOfDay()));

        User updatedUser = userRepository.save(user);

        return new UserProfileResponse(
                updatedUser.getUsername(),
                updatedUser.getFirstname(),
                updatedUser.getLastname(),
                updatedUser.getEmail(),
                updatedUser.getDob().toLocalDate()
        );
    }
}
