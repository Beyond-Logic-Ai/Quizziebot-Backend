package com.quizzka.backend.controller;

import com.quizzka.backend.entity.CustomQuizResult;
import com.quizzka.backend.payload.request.CustomQuizSubmissionRequest;
import com.quizzka.backend.service.CustomQuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/custom-quizzes")
public class CustomQuizController {

    @Autowired
    private CustomQuizService customQuizService;

    @PostMapping("/save")
    public ResponseEntity<?> saveCustomQuiz(@RequestBody CustomQuizSubmissionRequest submissionRequest) {
        CustomQuizResult result = customQuizService.saveCustomQuiz(submissionRequest);
        return ResponseEntity.ok(result);
    }
}
