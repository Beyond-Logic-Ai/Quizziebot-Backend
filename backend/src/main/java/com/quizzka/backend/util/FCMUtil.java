package com.quizzka.backend.util;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class FCMUtil {
    public void sendNotification(String token, String title, String message) {
        Message msg = Message.builder()
                .putData("title", title)
                .putData("message", message)
                .setToken(token)
                .build();

        try {
            FirebaseMessaging.getInstance().send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
