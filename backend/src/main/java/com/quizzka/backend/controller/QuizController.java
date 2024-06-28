package com.quizzka.backend.controller;

import com.quizzka.backend.entity.Question;
import com.quizzka.backend.entity.QuizResult;
import com.quizzka.backend.entity.UserResponse;
import com.quizzka.backend.payload.request.QuizSubmission;
import com.quizzka.backend.payload.request.UserResponseRequest;
import com.quizzka.backend.payload.response.QuestionResponse;
import com.quizzka.backend.service.QuestionService;
import com.quizzka.backend.service.QuizSubmissionService;
import com.quizzka.backend.service.UserResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/quiz")
public class QuizController {

    @Autowired
    private UserResponseService userResponseService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuizSubmissionService quizSubmissionService;

    @PostMapping("/save-responses")
    public UserResponse saveResponses(@RequestBody UserResponseRequest userResponseRequest) {
        userResponseService.saveUserResponse(
                userResponseRequest.getUserId(),
                userResponseRequest.getResponses(),
                userResponseRequest.getQuizStartTime(),
                userResponseRequest.getQuizEndTime()
        );
        return userResponseService.getLastUserResponse(userResponseRequest.getUserId());
    }

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
}
