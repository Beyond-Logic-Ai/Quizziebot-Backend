package com.quizzka.backend.payload.request;

import com.quizzka.backend.payload.request.helper.QuestionResponseOld;
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
    private List<QuestionResponseOld> responses;
    private LocalDateTime quizStartTime;
    private LocalDateTime quizEndTime;
}
