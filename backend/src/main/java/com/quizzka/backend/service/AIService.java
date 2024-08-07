package com.quizzka.backend.service;

import com.quizzka.backend.entity.Question;

import java.util.List;

public interface AIService {
    String fetchBriefFromAI(String topic) throws Exception;
    List<Question> fetchQuestionsFromAI(String topic) throws Exception;
    String fetchResponseFromAI(String prompt) throws Exception;
}
