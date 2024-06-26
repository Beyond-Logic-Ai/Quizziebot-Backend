package com.quizzka.backend.controller;

import com.quizzka.backend.entity.User;
import com.quizzka.backend.jwt.JwtUtil;
import com.quizzka.backend.payload.request.ForgotPasswordRequest;
import com.quizzka.backend.payload.request.LoginRequest;
import com.quizzka.backend.payload.request.ResetPasswordRequest;
import com.quizzka.backend.payload.request.SignUpRequest;
import com.quizzka.backend.payload.response.JwtResponse;
import com.quizzka.backend.payload.response.MessageResponse;
import com.quizzka.backend.payload.response.SignUpResponse;
import com.quizzka.backend.repository.UserRepository;
import com.quizzka.backend.service.AuthService;
import com.quizzka.backend.service.OtpService;
import com.quizzka.backend.service.UserResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/sendOtp")
    public ResponseEntity<String> sendOtp(@RequestParam String mobileNumber) {
        String otp = otpService.generateOtp();
        otpService.sendOtp(mobileNumber, otp);

        User user = userRepository.findByMobileNumber(mobileNumber)
                .orElse(User.builder().mobileNumber(mobileNumber).build());

        user.setOtp(otp);
        userRepository.save(user);

        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/validateOtp")
    public ResponseEntity<String> validateOtp(@RequestParam String mobileNumber, @RequestParam String otp) {
        User user = userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getOtp().equals(otp)) {
            String token = jwtUtil.generateToken(mobileNumber);
            user.setJwtToken(token);
            userRepository.save(user);

            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(401).body("Invalid OTP");
        }
    }
}
*/
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserResponseService userResponseService;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
        SignUpRequest signUpReq = authService.registerUser(signUpRequest);
        userResponseService.saveUserResponse(signUpReq.getId(), signUpReq.getQuizResponses(), signUpReq.getQuizStartTime(), signUpReq.getQuizEndTime());
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
}