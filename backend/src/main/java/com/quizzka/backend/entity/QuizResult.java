package com.quizzka.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Document(collection = "quiz_results")
public class QuizResult {
    @Id
    private String id;
    private String userId;
    private String quizId;
    private int score;
    private int correctAnswers;
    private int wrongAnswers;
    private int totalQuestions;
    private int xpGained;
    private int coinsGained;
    private int iqScore;
}