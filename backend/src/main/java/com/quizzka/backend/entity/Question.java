package com.quizzka.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Document(collection = "questions")
public class Question {
    @Id
    private String questionId;
    private String questionText;
    private List<String> options;
    private String correctOption;
    private String difficulty;
    private int timeLimit;
}
