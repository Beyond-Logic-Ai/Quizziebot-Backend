package com.quizzka.backend.service;

import com.quizzka.backend.entity.QuestionCollection;

import java.util.List;

public interface QuestionService {
    List<QuestionCollection> getQuestionsByCategory(String category);
    QuestionCollection saveQuestions(QuestionCollection questionCollection);
}
