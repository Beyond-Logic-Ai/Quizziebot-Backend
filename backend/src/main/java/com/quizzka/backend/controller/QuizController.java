package com.quizzka.backend.controller;

import com.quizzka.backend.entity.QuizResult;
import com.quizzka.backend.payload.request.QuizConfirmationRequest;
import com.quizzka.backend.payload.request.QuizSubmission;
import com.quizzka.backend.payload.request.TopicRequest;
import com.quizzka.backend.payload.response.ErrorResponse;
import com.quizzka.backend.payload.response.QuizGenerationResponse;
import com.quizzka.backend.payload.response.TopicBriefResponse;
import com.quizzka.backend.service.QuestionService;
import com.quizzka.backend.service.QuizSubmissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/questions")
    public ResponseEntity<?> getQuestions(@RequestParam String userId, @RequestParam String mode,
                                          @RequestParam(required = false) String category,
                                          @RequestParam(required = false) String difficulty) {
        Map<String, Object> response = questionService.getQuestions(userId, mode, category, difficulty);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitQuiz(@RequestBody QuizSubmission submission) {
        logger.info("Inside /submit");
        QuizResult result = quizSubmissionService.evaluateQuiz(submission);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/firstQuiz")
    public ResponseEntity<Map<String, Object>> getQuestions(@RequestParam String category, @RequestParam String difficulty) {
        Map<String, Object> response = questionService.getQuestionsByCategoryAndDifficulty(category, difficulty);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/brief")
    public ResponseEntity<?> getBrief(@RequestBody TopicRequest topicRequest) {
        try {
            TopicBriefResponse response = questionService.getBrief(topicRequest.getTopic());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Error fetching brief", e.getMessage()));
        }
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateQuiz(@RequestBody QuizConfirmationRequest confirmationRequest) {
        try {
            if (confirmationRequest.isConfirm()) {
                QuizGenerationResponse response = questionService.generateQuiz(confirmationRequest.getTopic(), confirmationRequest.getUserId());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(new ErrorResponse("Quiz generation not confirmed", "User did not confirm the quiz generation."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Error generating quiz", e.getMessage()));
        }
    }
}
