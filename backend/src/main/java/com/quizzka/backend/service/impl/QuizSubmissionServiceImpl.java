package com.quizzka.backend.service.impl;

import com.quizzka.backend.entity.Question;
import com.quizzka.backend.entity.QuizResult;
import com.quizzka.backend.entity.QuizSession;
import com.quizzka.backend.payload.request.QuizSubmission;
import com.quizzka.backend.payload.request.helper.Answer;
import com.quizzka.backend.repository.QuizResultRepository;
import com.quizzka.backend.repository.QuizSessionRepository;
import com.quizzka.backend.service.QuestionService;
import com.quizzka.backend.service.QuizSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class QuizSubmissionServiceImpl implements QuizSubmissionService {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuizResultRepository quizResultRepository;

    @Autowired
    private QuizSessionRepository quizSessionRepository;

    public QuizResult evaluateQuiz(QuizSubmission submission) {
        int correctAnswers = 0;
        int wrongAnswers = 0;
        int score = 0;
        int xpGained = 0;
        int iqScore = 0;

        // Fetch the relevant quiz session
        Optional<QuizSession> optionalQuizSession = quizSessionRepository.findByQuizId(submission.getQuizId());
        if (!optionalQuizSession.isPresent()) {
            throw new RuntimeException("Quiz session not found");
        }

        QuizSession quizSession = optionalQuizSession.get();

        // Evaluate answers and update question statuses
        for (Answer answer : submission.getAnswers()) {
            Question question = questionService.findQuestionById(answer.getQuestionId());
            if (question != null && question.getCorrectOption().equals(answer.getSelectedOption())) {
                correctAnswers++;
                score += 10;
                xpGained += 10;
            } else {
                wrongAnswers++;
            }

            // Update question status to answered
            quizSession.getQuestionStatuses().stream()
                    .filter(qs -> qs.getQuestionId().equals(answer.getQuestionId()))
                    .forEach(qs -> qs.setAnswered(true));
        }

        // Save the updated quiz session
        quizSessionRepository.save(quizSession);

        int totalQuestions = submission.getAnswers().size();
        iqScore = calculateIqScore(correctAnswers, totalQuestions);

        QuizResult result = new QuizResult();
        result.setUserId(submission.getUserId());
        result.setQuizId(submission.getQuizId());
        result.setScore(score);
        result.setCorrectAnswers(correctAnswers);
        result.setWrongAnswers(wrongAnswers);
        result.setTotalQuestions(totalQuestions);
        result.setXpGained(xpGained);
        result.setIqScore(iqScore);

        // Save the result in the database
        quizResultRepository.save(result);

        return result;
    }

    private int calculateIqScore(int correctAnswers, int totalQuestions) {
        return (int) (((double) correctAnswers / totalQuestions) * 100);
    }
}
