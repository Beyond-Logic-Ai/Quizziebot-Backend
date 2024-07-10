package com.quizzka.backend.service;

import com.quizzka.backend.entity.QuizResult;
import com.quizzka.backend.payload.request.QuizSubmission;

public interface QuizSubmissionService {
    QuizResult evaluateQuiz(QuizSubmission submission);
}
