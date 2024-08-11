package com.quizzka.backend.service.impl;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.quizzka.backend.entity.Notification;
import com.quizzka.backend.entity.User;
import com.quizzka.backend.entity.UserPlayStats;
import com.quizzka.backend.payload.response.LeaderboardEntry;
import com.quizzka.backend.repository.NotificationRepository;
import com.quizzka.backend.repository.UserPlayStatsRepository;
import com.quizzka.backend.repository.UserRepository;
import com.quizzka.backend.service.LeaderboardService;
import com.quizzka.backend.service.NotificationService;
import com.quizzka.backend.util.FCMUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);


    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPlayStatsRepository userPlayStatsRepository;

    @Autowired
    private LeaderboardService leaderboardService;

    @Autowired
    private FCMUtil fcmUtil;

    public void sendNotification(String userId, String title, String message) {
        Notification notification = Notification.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .createdAt(new Date())
                .isRead(false)
                .build();

        notificationRepository.save(notification);

        // Send push notification
        User user = userRepository.findById(userId).orElse(null);
        if (user != null && user.getFcmToken() != null) {
            fcmUtil.sendNotification(user.getFcmToken(), title, message);
        }
    }

    public List<Notification> getNotifications(String userId) {
        return notificationRepository.findByUserId(userId);
    }

    public void markAsRead(String notificationId) {
        Optional<Notification> notificationOptional = notificationRepository.findById(notificationId);
        if (!notificationOptional.isPresent()) {
            throw new RuntimeException("Notification not found");
        }

        Notification notification = notificationOptional.get();
        notification.setReadAt(new Date());
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void checkRanksAndStreaks() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            // Check if rank has dropped
            boolean rankDropped = hasRankDropped(user);
            if (rankDropped) {
                logger.info("Rank dropped for user: " + user.getUsername() + " (ID: " + user.getId() + ")");
                sendNotification(user.getId(), "Rank Dropped", "Your rank has dropped. Play more to improve your rank.");
            } else {
                logger.info("No rank drop for user: " + user.getUsername() + " (ID: " + user.getId() + ")");
            }

            // Check if streak reminder is needed
            if (needsStreakReminder(user)) {
                sendNotification(user.getId(), "Daily Streak Reminder", "You haven't played today. Maintain your streak by playing now!");
            }
        }

        // Update previous rank for all users
        updatePreviousRanks(users);
    }


    private boolean hasRankDropped(User user) {
        LeaderboardEntry currentRank = leaderboardService.getUserRank(user.getId());
        LeaderboardEntry previousRank = leaderboardService.getPreviousRank(user.getId());

        if (currentRank != null && previousRank != null) {
            boolean rankDropped = currentRank.getRank() > previousRank.getRank();
            logger.debug("User: " + user.getUsername() + ", Current Rank: " + currentRank.getRank() + ", Previous Rank: " + previousRank.getRank() + ", Rank Dropped: " + rankDropped);
            return rankDropped;
        }
        return false;
    }

    private boolean needsStreakReminder(User user) {
        UserPlayStats userPlayStats = userPlayStatsRepository.findByUserId(user.getId()).orElse(null);
        if (userPlayStats != null) {
            return !isSameDay(new Date(), userPlayStats.getLastPlayedDate());
        }
        return false;
    }

    private boolean isSameDay(Date date1, Date date2) {
        if (date2 == null) return false; // if user hasn't played one quiz yet

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private void updatePreviousRanks(List<User> users) {
        for (User user : users) {
            LeaderboardEntry currentRank = leaderboardService.getUserRank(user.getId());
            if (currentRank != null) {
                user.setPreviousRank(currentRank.getRank());
                userRepository.save(user);
            }
        }
    }
}
