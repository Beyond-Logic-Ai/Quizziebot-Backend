package com.quizzka.backend.entity;

import com.quizzka.backend.payload.request.helper.QuestionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Document(collection = "userResponses")
public class UserResponse {
    @Id
    private String id;
    private String userId;
    private List<QuestionResponse> responses;
    private LocalDateTime quizStartTime;
    private LocalDateTime quizEndTime;
    private long timeTaken; // in seconds
    private double percentage; // percentage of correct answers
    private int xpEarned; // XP earned based on correct answers

}
