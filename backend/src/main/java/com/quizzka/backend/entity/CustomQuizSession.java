package com.quizzka.backend.entity;

import com.quizzka.backend.entity.helper.QuestionStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "custom_quiz_sessions")
public class CustomQuizSession {
    @Id
    private String id;
    private String quizId;
    private String userId;
    private List<QuestionStatus> questionStatuses;
    private String mode;
    private Date createdAt;
    private Date updatedAt;
}
