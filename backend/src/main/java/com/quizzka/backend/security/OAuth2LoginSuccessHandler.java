package com.quizzka.backend.security;

import com.quizzka.backend.entity.User;
import com.quizzka.backend.jwt.JwtUtil;
import com.quizzka.backend.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public OAuth2LoginSuccessHandler(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();

        String email;
        if (oAuth2User instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) oAuth2User;
            email = oidcUser.getEmail();
        } else {
            email = oAuth2User.getAttribute("email");
        }

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = User.builder()
                    .email(email)
                    .firstname(oAuth2User.getAttribute("given_name"))
                    .lastname(oAuth2User.getAttribute("family_name"))
                    .username(email)
                    .password("") // Set default password if needed, handle appropriately in your logic
                    .createdAt(new Date())
                    .updatedAt(new Date())
                    .loginType("GOOGLE")
                    .build();
            return userRepository.save(newUser);
        });

        String jwtToken = jwtUtil.generateToken(new CustomOAuth2User(oAuth2User, user));

        response.addHeader("Authorization", "Bearer " + jwtToken);
        response.sendRedirect("/home");
    }
}