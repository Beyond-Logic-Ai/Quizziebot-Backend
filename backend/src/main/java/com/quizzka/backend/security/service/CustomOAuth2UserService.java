package com.quizzka.backend.security.service;

import com.quizzka.backend.entity.User;
import com.quizzka.backend.repository.UserRepository;
import com.quizzka.backend.security.CustomOAuth2User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomOAuth2UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = User.builder()
                    .email(email)
                    .firstname(oAuth2User.getAttribute("given_name"))
                    .lastname(oAuth2User.getAttribute("family_name"))
                    .username(email)
                    .password(passwordEncoder.encode("oauth2user"))
                    .createdAt(new Date())
                    .updatedAt(new Date())
                    .loginType("GOOGLE")
                    .build();
            return userRepository.save(newUser);
        });

        return new CustomOAuth2User(oAuth2User, user);
    }
}