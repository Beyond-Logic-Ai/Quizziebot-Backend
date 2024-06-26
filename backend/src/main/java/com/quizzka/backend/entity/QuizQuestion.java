package com.quizzka.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Document(collection = "quiz_questions")
public class QuizQuestion {
    @Id
    private String id;
    private String question;
    private Map<String, String> options;
    private String answer;
    private String topic;
}
