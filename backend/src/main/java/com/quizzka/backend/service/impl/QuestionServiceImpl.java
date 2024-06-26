package com.quizzka.backend.service.impl;

import com.quizzka.backend.entity.QuestionCollection;
import com.quizzka.backend.repository.QuestionRepository;
import com.quizzka.backend.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Override
    public List<QuestionCollection> getQuestionsByCategory(String category) {
        return questionRepository.findByCategory(category);
    }

    @Override
    public QuestionCollection saveQuestions(QuestionCollection questionCollection) {
        questionCollection.setCreatedAt(new Date());
        questionCollection.setUpdatedAt(new Date());
        return questionRepository.save(questionCollection);
    }
}
