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
        int coinsGained = 0;
        int iqScore = 0;

        // Try to find an existing quiz session or create a new one
        QuizSession quizSession = quizSessionRepository.findByQuizId(submission.getQuizId())
                .orElseGet(() -> {
                    QuizSession newQuizSession = new QuizSession();
                    newQuizSession.setQuizId(submission.getQuizId());
                    newQuizSession.setUserId(submission.getUserId());
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
                score += 10; // Score for correct answer
                xpGained += 15; // XP for correct answer (10 for correctness, 5 for attempt)
                coinsGained += 10; // Coins for correct answer
            } else {
                wrongAnswers++;
                xpGained += 5; // XP for attempting the question
                coinsGained += 5; // Coins for wrong answer
            }

            quizSession.getQuestionStatuses().stream()
                    .filter(qs -> qs.getQuestionId().equals(answer.getQuestionId()))
                    .forEach(qs -> qs.setAnswered(true));
        }

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
        result.setCoinsGained(coinsGained);
        result.setIqScore(iqScore);

        quizResultRepository.save(result);

        // Update user XP, coins, and score
        User user = userRepository.findById(submission.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
        user.setXp(user.getXp() + xpGained);
        user.setCoins(user.getCoins() + coinsGained);
        user.setScore(user.getScore() + score);
        user.setLeague(leaderboardService.getCurrentLeague(user.getXp()));
        userRepository.save(user);

        return result;
    }

    private int calculateIqScore(int correctAnswers, int totalQuestions) {
        return (int) (((double) correctAnswers / totalQuestions) * 100);
    }

}
