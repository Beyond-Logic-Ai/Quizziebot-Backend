package com.quizzka.backend.controller;

import com.quizzka.backend.entity.User;
import com.quizzka.backend.payload.request.*;
import com.quizzka.backend.payload.response.JwtResponse;
import com.quizzka.backend.payload.response.MessageResponse;
import com.quizzka.backend.payload.response.SignUpResponse;
import com.quizzka.backend.repository.UserRepository;
import com.quizzka.backend.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

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

    @GetMapping("/check-identifier")
    public ResponseEntity<?> checkIdentifier(
            @RequestParam String identifier,
            @RequestParam String identifierType) {

        Map<String, Object> response = new HashMap<>();
        boolean exists = false;

        try {
            switch (identifierType) {
                case "username":
                    exists = authService.checkUsername(identifier);
                    break;
                case "email":
                    exists = authService.checkEmail(identifier);
                    break;
                case "phoneNumber":
                    exists = authService.checkPhoneNumber(identifier);
                    break;
                default:
                    response.put("error", "Invalid identifierType");
                    return ResponseEntity.badRequest().body(response);
            }

            response.put("exists", exists);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error checking identifier: {}", e.getMessage());

            response.put("error", "An error occurred while checking the identifier.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
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