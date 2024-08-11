package com.quizzka.backend.service;

import com.quizzka.backend.entity.Notification;

import java.util.List;

public interface NotificationService {
    void sendNotification(String userId, String title, String message);
    List<Notification> getNotifications(String userId);
    void markAsRead(String notificationId);
    void checkRanksAndStreaks();
}
