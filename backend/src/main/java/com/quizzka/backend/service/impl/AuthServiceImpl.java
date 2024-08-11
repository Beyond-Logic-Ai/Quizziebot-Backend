package com.quizzka.backend.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.quizzka.backend.entity.QuizResult;
import com.quizzka.backend.entity.User;
import com.quizzka.backend.jwt.JwtUtil;
import com.quizzka.backend.payload.request.*;
import com.quizzka.backend.payload.response.JwtResponse;
import com.quizzka.backend.repository.QuizResultRepository;
import com.quizzka.backend.repository.UserRepository;
import com.quizzka.backend.service.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;

@Service
public class AuthServiceImpl implements AuthService {

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

    @Autowired
    private UserService userService;

    private AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public SignUpRequest registerUser(SignUpRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Username is already in use");
        }
        if (userRepository.existsByPhoneNumber(signUpRequest.getPhoneNumber())) {
            throw new RuntimeException("Phone number is already in use");
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
                .rememberMe(signUpRequest.isRememberMe())
                .fcmToken(signUpRequest.getFcmToken())
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();

        userRepository.save(user);
        signUpRequest.setId(user.getId());
//        QuizSubmission quizSubmission = signUpRequest.getQuizSubmission();
//        quizSubmission.setUserId(user.getId());
//        quizSubmission.setInitialQuiz(true);
//        QuizResult quizResult = quizSubmissionService.evaluateQuiz(quizSubmission);
        signUpRequest.setId(user.getId());
        return signUpRequest;
    }

    @Override
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getIdentifier(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getIdentifier());
            String jwt = jwtUtil.generateToken(userDetails);

            userService.updateLastLoginTime(userDetails.getUsername());

            return new JwtResponse(jwt);
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Incorrect username or password");
        }
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        User user = userRepository.findByEmail(forgotPasswordRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + forgotPasswordRequest.getEmail()));

        // Generate OTP
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);

        User updatedUser = user.toBuilder()
                .resetToken(String.valueOf(otp))
                .resetTokenExpiry(LocalDateTime.now().plusMinutes(15)) // OTP valid for 15 minutes
                .build();

        userRepository.save(updatedUser);

        emailService.sendEmail(forgotPasswordRequest.getEmail(), "Password Reset OTP", "Your OTP for password reset is: " + otp);
    }

    @Override
    public boolean validateOtp(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        return user.getResetToken().equals(otp) && !user.getResetTokenExpiry().isBefore(LocalDateTime.now());
    }

    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        User user = userRepository.findByEmail(resetPasswordRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + resetPasswordRequest.getEmail()));

        if (!user.getResetToken().equals(resetPasswordRequest.getOtp()) || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        User updatedUser = user.toBuilder()
                .password(passwordEncoder.encode(resetPasswordRequest.getNewPassword()))
                .resetToken(null)
                .resetTokenExpiry(null)
                .build();

        userRepository.save(updatedUser);
    }

    @Override
    public boolean checkUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean checkEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean checkPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public User findOrCreateUser(String email, GoogleIdToken.Payload payload) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = User.builder()
                    .email(email)
                    .firstname((String) payload.get("given_name"))
                    .lastname((String) payload.get("family_name"))
                    .username(email)
                    .password(passwordEncoder.encode("oauth2user"))
                    .createdAt(new Date())
                    .updatedAt(new Date())
                    .loginType("GOOGLE")
                    .build();
            return userRepository.save(newUser);
        });
    }
}
