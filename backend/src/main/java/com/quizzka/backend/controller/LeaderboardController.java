package com.quizzka.backend.controller;

import com.quizzka.backend.entity.User;
import com.quizzka.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController {

    @Autowired
    private UserService userService;

    @GetMapping("/league")
    public List<User> getLeagueLeaderboard(@RequestParam String userId) {
        User user = userService.findUserById(userId);
        List<User> users = userService.getUsersByLeague(user.getLeague());
        int rank = users.indexOf(user) + 1;
        System.out.println("Rank:" + rank);
        if (rank <= 25) {
            return users.subList(0, Math.min(25, users.size()));
        } else if (rank <= 50) {
            return users.subList(0, Math.min(50, users.size()));
        } else {
            return users.subList(0, Math.min(rank, users.size()));
        }
    }
}
