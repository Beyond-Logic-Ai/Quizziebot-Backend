package com.quizzka.backend.service.impl;

import com.quizzka.backend.controller.AuthController;
import com.quizzka.backend.entity.Question;
import com.quizzka.backend.entity.QuizResult;
import com.quizzka.backend.entity.QuizSession;
import com.quizzka.backend.entity.User;
import com.quizzka.backend.entity.helper.QuestionStatus;
import com.quizzka.backend.payload.request.QuizSubmission;
import com.quizzka.backend.payload.request.helper.Answer;
import com.quizzka.backend.repository.QuizResultRepository;
import com.quizzka.backend.repository.QuizSessionRepository;
import com.quizzka.backend.repository.UserRepository;
import com.quizzka.backend.service.LeaderboardService;
import com.quizzka.backend.service.QuestionService;
import com.quizzka.backend.service.QuizSubmissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class QuizSubmissionServiceImpl implements QuizSubmissionService {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuizResultRepository quizResultRepository;

    @Autowired
    private QuizSessionRepository quizSessionRepository;

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private LeaderboardService leaderboardService;

    public QuizResult evaluateQuiz(QuizSubmission submission) {
        int correctAnswers = 0;
        int wrongAnswers = 0;
        int score = 0;
        int xpGained = 0;
        int iqScore = 0;

        logger.info("Finding Existing quizSession");
        // Try to find an existing quiz session or create a new one
        QuizSession quizSession = quizSessionRepository.findByQuizId(submission.getQuizId())
                .orElseGet(() -> {
                    QuizSession newQuizSession = new QuizSession();
                    newQuizSession.setQuizId(submission.getQuizId());
                    newQuizSession.setUserId(submission.getUserId());
                    newQuizSession.setUpdatedAt(new Date());
                    newQuizSession.setQuestionStatuses(
                            submission.getAnswers().stream()
                                    .map(answer -> new QuestionStatus(answer.getQuestionId(), false))
                                    .collect(Collectors.toList())
                    );
                    return quizSessionRepository.save(newQuizSession);
                });

        for (Answer answer : submission.getAnswers()) {
            Question question = questionService.findQuestionById(answer.getQuestionId());
            if (question != null && question.getCorrectOption().equals(answer.getSelectedOption())) {
                correctAnswers++;
                score += 10;
                xpGained += 10;
            } else {
                wrongAnswers++;
            }

            quizSession.getQuestionStatuses().stream()
                    .forEach(qs -> {
                        submission.getAnswers().stream()
                                .filter(submissionAnswer -> submissionAnswer.getQuestionId().equals(qs.getQuestionId()))
                                .findFirst()
                                .ifPresent(submissionAnswer -> qs.setAnswered(submissionAnswer.isAnswered()));
                    });
        }

        logger.info("Saving the quiz session details at " + new Date());
        // Save the updated quiz session
        quizSession.setUpdatedAt(new Date());
        quizSession.getQuestionStatuses()
                        .forEach(e -> logger.info("status of current question: " + e));
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

        // Save the quiz result
        quizResultRepository.save(result);

        // Update user XP and score
        User user = userRepository.findById(submission.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
        user.setXp(user.getXp() + xpGained);
        user.setScore(user.getScore() + score);
        user.setLeague(leaderboardService.getCurrentLeague(user.getXp()));
        userRepository.save(user);

        return result;
    }

    private int calculateIqScore(int correctAnswers, int totalQuestions) {
        return (int) (((double) correctAnswers / totalQuestions) * 100);
    }
}
