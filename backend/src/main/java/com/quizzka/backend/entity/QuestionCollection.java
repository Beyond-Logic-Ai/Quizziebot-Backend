package com.quizzka.backend.entity;

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

@Document(collection = "question_collections")
public class QuestionCollection {
    @Id
    private String id;
    private String category;
    private List<Question> questions;
    private Date createdAt;
    private Date updatedAt;
}