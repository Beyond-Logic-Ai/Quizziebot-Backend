package com.quizzka.backend.util;

import com.quizzka.backend.entity.User;
import com.quizzka.backend.entity.UserPlayStats;
import com.quizzka.backend.repository.UserPlayStatsRepository;
import com.quizzka.backend.repository.UserRepository;
import com.quizzka.backend.service.LeaderboardService;
import com.quizzka.backend.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class ScheduledTasks {

    @Autowired
    private NotificationService notificationService;

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Scheduled(cron = "0 0/5 * * * ?") // Runs every day at midnight
    public void checkRanksAndStreaks() {
        logger.info("Scheduled task checkRanksAndStreaks started");
        notificationService.checkRanksAndStreaks();
        logger.info("Scheduled task checkRanksAndStreaks completed");
    }
}