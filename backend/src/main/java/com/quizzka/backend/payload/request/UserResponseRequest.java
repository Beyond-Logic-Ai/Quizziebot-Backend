package com.quizzka.backend.payload.request;

import com.quizzka.backend.entity.UserResponse;
import com.quizzka.backend.payload.request.helper.QuestionResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserResponseRequest {
    private String userId;
    private List<QuestionResponse> responses;
    private LocalDateTime quizStartTime;
    private LocalDateTime quizEndTime;
}
