package com.quizzka.backend.entity;

import com.quizzka.backend.entity.helper.QuestionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Document(collection = "quiz_sessions")
public class QuizSession {
    @Id
    private String id;
    private String quizId;
    private String userId;
    private List<QuestionStatus> questionStatuses;
    private Date createdAt;
    private Date updatedAt;
}
