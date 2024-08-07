package com.quizzka.backend.service;

import com.quizzka.backend.entity.Question;
import com.quizzka.backend.entity.QuestionCollection;
import com.quizzka.backend.payload.response.QuizGenerationResponse;
import com.quizzka.backend.payload.response.TopicBriefResponse;

import java.util.List;
import java.util.Map;

public interface QuestionService {
    List<QuestionCollection> getQuestionsByCategory(String category);
    QuestionCollection saveQuestions(QuestionCollection questionCollection);
    Map<String, Object> getQuestions(String userId, String mode, String category, String difficulty);
    Question findQuestionById(String questionId);
    Map<String, Object> getQuestionsByCategoryAndDifficulty(String category, String difficulty);
    List<Question> getQuestionsByCategoryAsQuestions(String category);
    TopicBriefResponse getBrief(String topic) throws Exception;
    QuizGenerationResponse generateQuiz(String topic, String userId) throws Exception;
}
