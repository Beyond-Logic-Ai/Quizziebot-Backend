package com.quizzka.backend.payload.response;

import com.quizzka.backend.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizGenerationResponse {
    private String quizId;
    private List<Question> questions;
}
