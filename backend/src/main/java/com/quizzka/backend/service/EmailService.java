package com.quizzka.backend.service;

public interface EmailService {
    void sendEmail(String to, String subject, String text);
}
