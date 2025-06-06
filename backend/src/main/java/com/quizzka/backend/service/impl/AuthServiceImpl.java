package com.quizzka.backend.service.impl;

import com.quizzka.backend.entity.QuizResult;
import com.quizzka.backend.entity.User;
import com.quizzka.backend.jwt.JwtUtil;
import com.quizzka.backend.payload.request.*;
import com.quizzka.backend.payload.response.JwtResponse;
import com.quizzka.backend.repository.QuizResultRepository;
import com.quizzka.backend.repository.UserRepository;
import com.quizzka.backend.service.AuthService;
import com.quizzka.backend.service.EmailService;
import com.quizzka.backend.service.QuizSubmissionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private QuizResultRepository quizResultRepository;

    @Autowired
    private QuizSubmissionService quizSubmissionService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public SignUpRequest registerUser(SignUpRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Username is already in use");
        }

        User user = User.builder()
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .phoneNumber(signUpRequest.getPhoneNumber())
                .firstname(signUpRequest.getFirstname())
                .lastname(signUpRequest.getLastname())
                .username(signUpRequest.getUsername())
                .gender(signUpRequest.getGender())
                .dob(signUpRequest.getDob())
                .accountType(signUpRequest.getAccountType())
                .age(signUpRequest.getAge())
                .country(signUpRequest.getCountry())
                .loginType(signUpRequest.getLoginType())
                .build();

        userRepository.save(user);
        signUpRequest.setId(user.getId());
        QuizSubmission quizSubmission = signUpRequest.getQuizSubmission();
        quizSubmission.setUserId(user.getId());
        quizSubmission.setInitialQuiz(true);
        QuizResult quizResult = quizSubmissionService.evaluateQuiz(quizSubmission);
        signUpRequest.setId(user.getId());

        return signUpRequest;
    }

    @Override
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getIdentifier(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getIdentifier());
        String jwt = jwtUtil.generateToken(userDetails);

        return new JwtResponse(jwt);
    }


    @Override
    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        User user = userRepository.findByEmail(forgotPasswordRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + forgotPasswordRequest.getEmail()));

        String token = UUID.randomUUID().toString();
        User updatedUser = user.toBuilder()
                .resetToken(token)
                .resetTokenExpiry(LocalDateTime.now().plusHours(1)) // Token valid for 1 hour
                .build();

        userRepository.save(updatedUser);


        String resetLink = "http://localhost:8081/reset-password.html?token=" + token;
        emailService.sendEmail(forgotPasswordRequest.getEmail(), "Password Reset Request", "Click the link to reset your password: " + resetLink);
    }

    @Override
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        User user = userRepository.findByResetToken(resetPasswordRequest.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid or expired password reset token"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Password reset token has expired");
        }

        User updatedUser = user.toBuilder()
                .password(passwordEncoder.encode(resetPasswordRequest.getNewPassword()))
                .resetToken(null)
                .resetTokenExpiry(null)
                .build();

        userRepository.save(updatedUser);
    }

    @Override
    public boolean validateResetToken(String token) {
        Optional<User> userOptional = userRepository.findByResetToken(token);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return !user.getResetTokenExpiry().isBefore(LocalDateTime.now());
        }
        return false;
    }
}
