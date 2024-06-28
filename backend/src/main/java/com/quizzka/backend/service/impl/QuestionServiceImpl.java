package com.quizzka.backend.service.impl;

import com.quizzka.backend.entity.Question;
import com.quizzka.backend.entity.QuestionCollection;
import com.quizzka.backend.entity.QuizSession;
import com.quizzka.backend.repository.QuestionCollectionRepository;
import com.quizzka.backend.repository.QuestionRepository;
import com.quizzka.backend.repository.QuizSessionRepository;
import com.quizzka.backend.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionCollectionRepository questionCollectionRepository;

    @Autowired
    private QuizSessionRepository quizSessionRepository;

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

    @Override
    public Map<String, Object> getQuestionsByCategoryAndDifficulty(String userId, String category, String difficulty) {
        Optional<QuizSession> optionalQuizSession = quizSessionRepository.findByUserId(userId);
        List<String> answeredQuestionIds = optionalQuizSession
                .map(QuizSession::getQuestionIds)
                .orElse(new ArrayList<>());

        List<QuestionCollection> collections = questionCollectionRepository.findByCategory(category);
        List<Question> allQuestions = collections.stream()
                .flatMap(collection -> collection.getQuestions().stream())
                .filter(question -> question.getDifficulty().equalsIgnoreCase(difficulty))
                .filter(question -> !answeredQuestionIds.contains(question.getQuestionId()))
                .collect(Collectors.toList());

        // Shuffle and limit to 10 questions
        Collections.shuffle(allQuestions);
        List<Question> selectedQuestions = allQuestions.stream().limit(10).collect(Collectors.toList());

        // Generate a unique quizId
        String quizId = UUID.randomUUID().toString();

        // Save the session with the newly selected question IDs
        QuizSession quizSession = new QuizSession();
        quizSession.setQuizId(quizId);
        quizSession.setUserId(userId);
        quizSession.setQuestionIds(selectedQuestions.stream().map(Question::getQuestionId).collect(Collectors.toList()));
        quizSessionRepository.save(quizSession);

        Map<String, Object> response = new HashMap<>();
        response.put("quizId", quizId);
        response.put("questions", selectedQuestions);

        return response;
    }

    @Override
    public Question findQuestionById(String questionId) {
        List<QuestionCollection> collections = questionCollectionRepository.findAll();
        for (QuestionCollection collection : collections) {
            for (Question question : collection.getQuestions()) {
                if (question.getQuestionId().equals(questionId)) {
                    return question;
                }
            }
        }
        return null;
    }
}
