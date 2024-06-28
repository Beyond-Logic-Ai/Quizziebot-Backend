package com.quizzka.backend.service.impl;

import com.quizzka.backend.entity.Question;
import com.quizzka.backend.entity.QuizResult;
import com.quizzka.backend.payload.request.QuizSubmission;
import com.quizzka.backend.payload.request.helper.Answer;
import com.quizzka.backend.repository.QuizResultRepository;
import com.quizzka.backend.service.QuestionService;
import com.quizzka.backend.service.QuizSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class QuizSubmissionServiceImpl implements QuizSubmissionService {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuizResultRepository quizResultRepository;

    public QuizResult evaluateQuiz(QuizSubmission submission) {
        int correctAnswers = 0;
        int wrongAnswers = 0;
        int score = 0;
        int xpGained = 0;
        int iqScore = 0;

        for (Answer answer : submission.getAnswers()) {
            Question question = questionService.findQuestionById(answer.getQuestionId());
            if (question != null && question.getCorrectOption().equals(answer.getSelectedOption())) {
                correctAnswers++;
                score += 10; // example scoring logic
                xpGained += 10; // example XP logic
            } else {
                wrongAnswers++;
            }
        }

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
        // Implement your IQ score calculation logic here
        return (int) (((double) correctAnswers / totalQuestions) * 100);
    }
}
