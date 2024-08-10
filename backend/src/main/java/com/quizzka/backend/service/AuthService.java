package com.quizzka.backend.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.quizzka.backend.entity.User;
import com.quizzka.backend.payload.request.ForgotPasswordRequest;
import com.quizzka.backend.payload.request.LoginRequest;
import com.quizzka.backend.payload.request.ResetPasswordRequest;
import com.quizzka.backend.payload.request.SignUpRequest;
import com.quizzka.backend.payload.response.JwtResponse;

public interface AuthService {
    SignUpRequest registerUser(SignUpRequest signUpRequest);
    JwtResponse authenticateUser(LoginRequest loginRequest);
    void forgotPassword(ForgotPasswordRequest forgotPasswordRequest);
    boolean validateOtp(String email, String otp);
    void resetPassword(ResetPasswordRequest resetPasswordRequest);
//    boolean validateResetToken(String token);
    boolean checkUsername(String username);
    boolean checkEmail(String email);
    boolean checkPhoneNumber(String phoneNumber);
    User findOrCreateUser(String email, GoogleIdToken.Payload payload);
}
