package com.quizzka.backend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "custom_quiz_collections")
public class CustomQuizCollection {
    @Id
    private String id;
    private String category;
    private List<Question> questions;
    private Date createdAt;
    private Date updatedAt;
}
