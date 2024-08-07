package com.quizzka.backend.service;

import com.quizzka.backend.entity.CustomQuizResult;
import com.quizzka.backend.payload.request.CustomQuizSubmissionRequest;

public interface CustomQuizService {
    CustomQuizResult saveCustomQuiz(CustomQuizSubmissionRequest submissionRequest);
}
