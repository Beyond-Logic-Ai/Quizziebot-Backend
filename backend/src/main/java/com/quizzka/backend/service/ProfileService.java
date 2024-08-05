package com.quizzka.backend.service;

import com.quizzka.backend.entity.User;
import com.quizzka.backend.payload.request.ProfileUpdateRequest;
import com.quizzka.backend.payload.response.ProfileResponse;
import com.quizzka.backend.payload.response.UserProfileResponse;

public interface ProfileService {
    ProfileResponse getUserProfile(String userId);
    UserProfileResponse updateProfile(ProfileUpdateRequest profileUpdateRequest);
}
