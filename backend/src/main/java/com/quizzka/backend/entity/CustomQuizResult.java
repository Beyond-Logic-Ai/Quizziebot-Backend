package com.quizzka.backend.entity;

import com.quizzka.backend.payload.request.helper.Answer;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "custom_quiz_results")
public class CustomQuizResult {
    @Id
    private String id;
    private String userId;
    private String quizId;
    private int correctAnswers;
    private int wrongAnswers;
    private long totalTimeTaken; // in seconds
    private Date createdAt;
}
