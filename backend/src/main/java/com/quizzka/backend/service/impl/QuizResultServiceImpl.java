package com.quizzka.backend.service.impl;

import com.quizzka.backend.entity.QuizResult;
import com.quizzka.backend.repository.QuizResultRepository;
import com.quizzka.backend.service.QuizResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizResultServiceImpl implements QuizResultService {

    @Autowired
    private QuizResultRepository quizResultRepository;

    @Override
    public List<QuizResult> findByUserId(String userId) {
        List<QuizResult> result = quizResultRepository.findByUserId(userId);
        System.out.println(result);
        return result;
    }
}
