package com.quizzka.backend.service.impl;

import com.quizzka.backend.entity.User;
import com.quizzka.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeagueService {

    @Autowired
    private UserRepository userRepository;

    public void updateXp(String userId, int xpEarned) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setTotalXp(user.getTotalXp() + xpEarned);
        userRepository.save(user);
    }

    @Scheduled(cron = "0 0 12 * * SUN")
    public void promoteUsers() {
        promoteLeague("bronze", "silver");
        promoteLeague("silver", "gold");
        // Add more leagues as needed
    }

    private void promoteLeague(String fromLeague, String toLeague) {
        List<User> users = userRepository.findTop10ByLeagueOrderByTotalXpDesc(fromLeague);
        for (User user : users) {
            user.setLeague(toLeague);
            userRepository.save(user);
        }
    }
}
