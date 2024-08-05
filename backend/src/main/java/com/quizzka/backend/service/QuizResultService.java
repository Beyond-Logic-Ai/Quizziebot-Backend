package com.quizzka.backend.service;

import com.quizzka.backend.entity.QuizResult;

import java.util.List;

public interface QuizResultService {
    List<QuizResult> findByUserId(String userId);
}
