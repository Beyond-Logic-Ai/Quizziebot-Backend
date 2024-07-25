package com.quizzka.backend.service;

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
}
