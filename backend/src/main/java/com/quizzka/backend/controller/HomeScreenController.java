package com.quizzka.backend.controller;

import com.quizzka.backend.payload.response.HomeScreenResponse;
import com.quizzka.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
public class HomeScreenController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<HomeScreenResponse> getHomeScreenData(@AuthenticationPrincipal UserDetails userDetails) {
        HomeScreenResponse response = userService.getHomeScreenData(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
}
