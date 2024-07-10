package com.quizzka.backend.controller;

import com.quizzka.backend.entity.User;
import com.quizzka.backend.payload.response.LeaderboardEntry;
import com.quizzka.backend.repository.UserRepository;
import com.quizzka.backend.service.LeaderboardService;
import com.quizzka.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    @Autowired
    private LeaderboardService leaderboardService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/global")
    public ResponseEntity<List<LeaderboardEntry>> getGlobalLeaderboard() {
        return ResponseEntity.ok(leaderboardService.getGlobalLeaderboard());
    }

    @GetMapping("/local")
    public ResponseEntity<List<LeaderboardEntry>> getLocalLeaderboard(@RequestParam String country) {
        return ResponseEntity.ok(leaderboardService.getCountryLeaderboard(country));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<LeaderboardEntry> getUserRank(@PathVariable String userId) {
        LeaderboardEntry entry = leaderboardService.getUserRank(userId);
        if (entry != null) {
            return ResponseEntity.ok(entry);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/league")
    public ResponseEntity<String> getCurrentLeague(@RequestParam String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            String league = leaderboardService.getCurrentLeague(user.getXp());
            return ResponseEntity.ok(league);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
