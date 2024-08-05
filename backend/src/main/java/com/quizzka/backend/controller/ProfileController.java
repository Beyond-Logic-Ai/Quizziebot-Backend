package com.quizzka.backend.controller;

import com.quizzka.backend.entity.User;
import com.quizzka.backend.payload.request.ProfileUpdateRequest;
import com.quizzka.backend.payload.response.ProfileResponse;
import com.quizzka.backend.payload.response.UserProfileResponse;
import com.quizzka.backend.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable String userId) {
        try {
            ProfileResponse profileResponse = profileService.getUserProfile(userId);
            return ResponseEntity.ok(profileResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/update")
    public ResponseEntity<UserProfileResponse> updateProfile(@RequestBody ProfileUpdateRequest profileUpdateRequest) {
        UserProfileResponse updatedProfile = profileService.updateProfile(profileUpdateRequest);
        return ResponseEntity.ok(updatedProfile);
    }
}