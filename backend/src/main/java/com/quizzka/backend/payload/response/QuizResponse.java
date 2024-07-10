package com.quizzka.backend.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizResponse {
    private String questionId;
    private String selectedOption;
    private int timeTaken;
    private boolean isCorrect;
}
