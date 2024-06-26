package com.quizzka.backend.controller;

import com.quizzka.backend.entity.QuizQuestion;
import com.quizzka.backend.service.QuestionFetchingService;
import com.quizzka.backend.service.QuizQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class QuizQuestionController {

    @Autowired
    private QuizQuestionService quizQuestionService;

    @Autowired
    private QuestionFetchingService questionFetchingService;

    @GetMapping("/fetch-quiz")
    public String fetchQuiz(@RequestParam String topic) {
        quizQuestionService.fetchAndSaveQuiz(topic);
        return "Quiz fetched and saved successfully for topic: " + topic;
    }

    @GetMapping("/questions")
    public List<QuizQuestion> fetchQuestions(@RequestParam String topic) {
        return quizQuestionService.getQuestionsByTopic(topic);
    }

    @GetMapping("/saveQuiz")
    public String fetchQuestionsFromLLM() {
        questionFetchingService.fetchQuestionsFromLLM();
        return "Quiz fetched and saved successfully from Gemini LLM";
    }
}
