package com.quizzka.backend.controller;

import com.quizzka.backend.entity.QuizResult;
import com.quizzka.backend.payload.request.QuizSubmission;
import com.quizzka.backend.service.QuestionService;
import com.quizzka.backend.service.QuizSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuizSubmissionService quizSubmissionService;


    @GetMapping("/questions")
    public ResponseEntity<?> getQuestions(@RequestParam String userId, @RequestParam String category, @RequestParam String difficulty) {
        Map<String, Object> response = questionService.getQuestionsByCategoryAndDifficulty(userId, category, difficulty);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitQuiz(@RequestBody QuizSubmission submission) {
        QuizResult result = quizSubmissionService.evaluateQuiz(submission);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/firstQuiz")
    public ResponseEntity<Map<String, Object>> getQuestions(@RequestParam String category, @RequestParam String difficulty) {
        Map<String, Object> response = questionService.getQuestionsByCategoryAndDifficulty(category, difficulty);
        return ResponseEntity.ok(response);
    }
}
