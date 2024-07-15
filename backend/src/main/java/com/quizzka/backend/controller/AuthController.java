package com.quizzka.backend.controller;

import com.quizzka.backend.entity.User;
import com.quizzka.backend.payload.request.*;
import com.quizzka.backend.payload.response.JwtResponse;
import com.quizzka.backend.payload.response.MessageResponse;
import com.quizzka.backend.payload.response.SignUpResponse;
import com.quizzka.backend.repository.UserRepository;
import com.quizzka.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Validated @RequestBody SignUpRequest signUpRequest) {
        SignUpRequest signUpReq = authService.registerUser(signUpRequest);
        return ResponseEntity.ok(new SignUpResponse("User registered successfully!", signUpReq.getId()));
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        authService.forgotPassword(forgotPasswordRequest);
        return ResponseEntity.ok(new MessageResponse("OTP sent to your email!"));
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<?> validateOtp(@RequestBody ValidateOtpRequest validateOtpRequest) {
        boolean isValid = authService.validateOtp(validateOtpRequest.getEmail(), validateOtpRequest.getOtp());
        if (isValid) {
            return ResponseEntity.ok(new MessageResponse("OTP is valid."));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid or expired OTP."));
        }
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        authService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok(new MessageResponse("Password reset successfully!"));
    }




    /*
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        authService.forgotPassword(forgotPasswordRequest);
        return ResponseEntity.ok(new MessageResponse("Password reset link sent!"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        authService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok(new MessageResponse("Password reset successfully!"));
    }

    @GetMapping("/reset-password")
    public ResponseEntity<?> validateResetToken(@RequestParam("token") String token) {
        boolean isValid = authService.validateResetToken(token);
        if (isValid) {
            return ResponseEntity.ok(new MessageResponse("Token is valid."));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid or expired token."));
        }
    }
    */

}