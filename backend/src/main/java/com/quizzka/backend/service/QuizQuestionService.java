package com.quizzka.backend.service;

import com.quizzka.backend.entity.QuizQuestion;

import java.util.List;

public interface QuizQuestionService {
    void fetchAndSaveQuiz(String topic);
    List<QuizQuestion> getQuestionsByTopic(String topic);
}
