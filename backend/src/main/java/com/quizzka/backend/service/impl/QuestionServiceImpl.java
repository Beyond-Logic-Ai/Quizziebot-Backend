package com.quizzka.backend.service.impl;

import com.quizzka.backend.entity.Question;
import com.quizzka.backend.entity.QuestionCollection;
import com.quizzka.backend.entity.QuizSession;
import com.quizzka.backend.entity.helper.QuestionStatus;
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
        // Fetch all quiz sessions for the user
        List<QuizSession> quizSessions = quizSessionRepository.findByUserId(userId);

        // Extract answered question IDs from all sessions
        List<String> answeredQuestionIds = quizSessions.stream()
                .flatMap(session -> session.getQuestionStatuses().stream())
                .filter(QuestionStatus::isAnswered) // Filter for answered questions
                .map(QuestionStatus::getQuestionId) // Extract question IDs
                .toList();

        // Fetch questions from the db
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
        List<QuestionStatus> questionStatuses = selectedQuestions.stream()
                .map(question -> new QuestionStatus(question.getQuestionId(), false)) // Mark new questions as unanswered
                .toList();

        // Save the session with the newly selected question IDs
        QuizSession quizSession = new QuizSession();
        quizSession.setQuizId(quizId);
        quizSession.setUserId(userId);
        quizSession.setQuestionStatuses(questionStatuses);
        quizSessionRepository.save(quizSession);

        Map<String, Object> response = new HashMap<>();
        response.put("quizId", quizId);
        response.put("questions", selectedQuestions);

        return response;
    }

    @Override
    public Map<String, Object> getQuestionsByCategoryAndDifficulty(String category, String difficulty) {
        // Fetch questions from the repository
        List<QuestionCollection> collections = questionCollectionRepository.findByCategory(category);
        List<Question> allQuestions = collections.stream()
                .flatMap(collection -> collection.getQuestions().stream())
                .filter(question -> question.getDifficulty().equalsIgnoreCase(difficulty))
                .collect(Collectors.toList());

        // Shuffle and limit to 10 questions
        Collections.shuffle(allQuestions);
        List<Question> selectedQuestions = allQuestions.stream().limit(10).collect(Collectors.toList());

        // Generate a unique quizId
        String quizId = UUID.randomUUID().toString();

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
