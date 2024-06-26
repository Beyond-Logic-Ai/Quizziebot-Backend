package com.quizzka.backend.service;

public interface OtpService {
    String generateOtp();
    void sendOtp(String mobileNumber, String otp);
}
