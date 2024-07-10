package com.quizzka.backend.payload.request;

import com.quizzka.backend.payload.request.helper.Answer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class QuizSubmission {
    private String userId;
    private String quizId;
    private List<Answer> answers;
    private boolean isInitialQuiz;
}
