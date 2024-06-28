package com.quizzka.backend.service;

import com.quizzka.backend.entity.UserResponse;
import com.quizzka.backend.payload.request.helper.QuestionResponseOld;

import java.time.LocalDateTime;
import java.util.List;

public interface UserResponseService {
    void saveUserResponse(String userId, List<QuestionResponseOld> responses, LocalDateTime startTime, LocalDateTime endTime);
    UserResponse getLastUserResponse(String userId);
}
