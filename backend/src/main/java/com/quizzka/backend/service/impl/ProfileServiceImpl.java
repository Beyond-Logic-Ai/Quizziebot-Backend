package com.quizzka.backend.service.impl;

import com.quizzka.backend.entity.QuizResult;
import com.quizzka.backend.entity.User;
import com.quizzka.backend.entity.UserPlayStats;
import com.quizzka.backend.jwt.JwtUtil;
import com.quizzka.backend.payload.request.ProfileUpdateRequest;
import com.quizzka.backend.payload.response.LeaderboardEntry;
import com.quizzka.backend.payload.response.ProfileResponse;
import com.quizzka.backend.payload.response.UserProfileResponse;
import com.quizzka.backend.repository.UserRepository;
import com.quizzka.backend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private LeaderboardService leaderboardService;

    @Autowired
    private S3Client s3Client;

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
        response.setLeague(leaderboardService.getCurrentLeague(user.getXp()));

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

        boolean isUsernameUpdated = false;

        if (profileUpdateRequest.getUsername().isPresent()) {
            String newUsername = profileUpdateRequest.getUsername().get();
            if (!user.getUsername().equals(newUsername)) {
                if (userRepository.existsByUsername(newUsername)) {
                    throw new RuntimeException("Username is already in use");
                }
                user.setUsername(newUsername);
                isUsernameUpdated = true;
            }
        }

        profileUpdateRequest.getFirstname().ifPresent(user::setFirstname);
        profileUpdateRequest.getLastname().ifPresent(user::setLastname);
        profileUpdateRequest.getEmail().ifPresent(user::setEmail);
        profileUpdateRequest.getDob().ifPresent(dob -> user.setDob(dob.atStartOfDay()));

        // Handle profile picture upload
        if (profileUpdateRequest.getProfilePicture().isPresent()) {
            MultipartFile file = profileUpdateRequest.getProfilePicture().get();
            String key = "profiles/" + user.getId() + "_" + file.getOriginalFilename();

            try {
                s3Client.putObject(PutObjectRequest.builder()
                                .bucket("profilesquizee")
                                .key(key)
                                .contentType(file.getContentType())
                                .build(),
                        software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));
                String profilePictureUrl = "https://profilesquizee.s3.us-east-1.amazonaws.com/" + key;
                user.setProfilePictureUrl(profilePictureUrl);
            } catch (IOException e) {
                throw new RuntimeException("Error uploading profile picture", e);
            }
        }

        User updatedUser = userRepository.save(user);

        // Update username in UserPlayStats
        if (isUsernameUpdated) {
            updateUserPlayStatsUsername(user.getId(), updatedUser.getUsername());
        }

        String newJwtToken = null;
        if (isUsernameUpdated) {
            newJwtToken = jwtUtil.generateToken(updatedUser);
        }

        return new UserProfileResponse(
                updatedUser.getUsername(),
                updatedUser.getFirstname(),
                updatedUser.getLastname(),
                updatedUser.getEmail(),
                updatedUser.getDob().toLocalDate(),
                newJwtToken,
                updatedUser.getProfilePictureUrl() // Return the profile picture URL
        );
    }


    private void updateUserPlayStatsUsername(String userId, String newUsername) {
        UserPlayStats userPlayStats = userPlayStatsService.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User play stats not found"));
        userPlayStats.setUserName(newUsername);
        userPlayStatsService.save(userPlayStats);
    }
}
