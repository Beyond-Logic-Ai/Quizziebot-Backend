package com.quizzka.backend.service.impl;

import com.quizzka.backend.controller.AuthController;
import com.quizzka.backend.entity.*;
import com.quizzka.backend.entity.helper.QuestionStatus;
import com.quizzka.backend.payload.request.QuizSubmission;
import com.quizzka.backend.payload.request.helper.Answer;
import com.quizzka.backend.payload.response.LeaderboardEntry;
import com.quizzka.backend.repository.QuizResultRepository;
import com.quizzka.backend.repository.QuizSessionRepository;
import com.quizzka.backend.repository.UserPlayStatsRepository;
import com.quizzka.backend.repository.UserRepository;
import com.quizzka.backend.service.LeaderboardService;
import com.quizzka.backend.service.QuestionService;
import com.quizzka.backend.service.QuizSubmissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
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

    @Autowired
    private UserPlayStatsRepository userPlayStatsRepository;

    @Override
    public QuizResult evaluateQuiz(QuizSubmission submission) {
        int correctAnswers = 0;
        int wrongAnswers = 0;
        int score = 0;
        int xpGained = 0;
        int coinsGained = 0;
        int iqScore = 0;
        long totalTimeTaken = 0;

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
                    newQuizSession.setCreatedAt(new Date());
                    newQuizSession.setUpdatedAt(new Date());
                    return quizSessionRepository.save(newQuizSession);
                });

        Map<String, Question> questionMap = submission.getAnswers().stream()
                .map(answer -> questionService.findQuestionById(answer.getQuestionId()))
                .collect(Collectors.toMap(Question::getQuestionId, Function.identity()));

        for (Answer answer : submission.getAnswers()) {
            Question question = questionMap.get(answer.getQuestionId());
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

            totalTimeTaken += answer.getTimeTaken();

            quizSession.getQuestionStatuses().stream()
                    .filter(qs -> qs.getQuestionId().equals(answer.getQuestionId()))
                    .forEach(qs -> qs.setAnswered(true));
        }

        quizSessionRepository.save(quizSession);

        int totalQuestions = submission.getAnswers().size();
        iqScore = calculateIqScore(submission.getAnswers(), questionMap);

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
        result.setCreatedAt(new Date());
        result.setTotalTimeSpent(totalTimeTaken);

        quizResultRepository.save(result);

        // Update user XP, coins, and score
        User user = userRepository.findById(submission.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
        user.setXp(user.getXp() + xpGained);
        user.setCoins(user.getCoins() + coinsGained);
        user.setScore(user.getScore() + score);
        user.setLeague(leaderboardService.getCurrentLeague(user.getXp()));
        userRepository.save(user);

        // Update user play stats
        updateUserPlayStats(user, quizSession.getMode(), iqScore, totalTimeTaken);

        return result;
    }

    private void updateUserPlayStats(User user, String mode, int newIqScore, long totalTimeTaken) {
        UserPlayStats userPlayStats = userPlayStatsRepository.findByUserId(user.getId()).orElseGet(() -> {
            UserPlayStats newUserPlayStats = new UserPlayStats();
            newUserPlayStats.setUserId(user.getId());
            newUserPlayStats.setUserName(user.getUsername());
            newUserPlayStats.setStreak(0); // Initialize streak to 0
            newUserPlayStats.setLastPlayedDate(new Date());
            return newUserPlayStats;
        });

        Date today = new Date();
        boolean isSameDay = isSameDay(today, userPlayStats.getLastPlayedDate());

        if (!isSameDay) {
            long diffInMillies = Math.abs(today.getTime() - userPlayStats.getLastPlayedDate().getTime());
            long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            if (diffInDays == 1) {
                userPlayStats.setStreak(userPlayStats.getStreak() + 1); // Increment streak
            } else {
                userPlayStats.setStreak(1); // Reset streak
            }
            userPlayStats.setLastPlayedDate(today);
        }

        if ("classic".equalsIgnoreCase(mode)) {
            userPlayStats.setClassicPlays(userPlayStats.getClassicPlays() + 1);
        } else if ("arcade".equalsIgnoreCase(mode)) {
            userPlayStats.setArcadePlays(userPlayStats.getArcadePlays() + 1);
        }

        // Update overall IQ
        double newOverallIq = calculateOverallIq(userPlayStats.getOverallIq(), userPlayStats.getTotalPlays(), newIqScore);
        userPlayStats.setOverallIq(newOverallIq);
        userPlayStats.setTotalPlays(userPlayStats.getTotalPlays() + 1);

        // Update fastest record
        if (userPlayStats.getFastestRecord() == 0 || totalTimeTaken < userPlayStats.getFastestRecord()) {
            userPlayStats.setFastestRecord((int) totalTimeTaken);
        }

        // Update total time spent
        userPlayStats.setTotalTimeSpent(userPlayStats.getTotalTimeSpent() + totalTimeTaken);

        // Update top 3 positions
        LeaderboardEntry currentRank = leaderboardService.getUserRank(user.getId());
        if (currentRank.getRank() <= 3) {
            userPlayStats.setTopPositions(userPlayStats.getTopPositions() + 1);
        }

        userPlayStatsRepository.save(userPlayStats);
    }

    private double calculateOverallIq(double currentOverallIq, int totalPlays, int newIqScore) {
        return ((currentOverallIq * totalPlays) + newIqScore) / (totalPlays + 1);
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private int calculateIqScore(List<Answer> answers, Map<String, Question> questionMap) {
        double baseIq = 100;
        double totalWeight = 0;
        double score = 0;

        for (Answer answer : answers) {
            Question question = questionMap.get(answer.getQuestionId());
            double weight = getWeightForDifficulty(question.getDifficulty());
            totalWeight += weight;
            if (question.getCorrectOption().equals(answer.getSelectedOption())) {
                score += weight * (1 - ((double) answer.getTimeTaken() / question.getTimeLimit()));
            }
        }

        return (int) (baseIq + (score / totalWeight) * 15);
    }

    private double getWeightForDifficulty(String difficulty) {
        switch (difficulty.toLowerCase()) {
            case "easy":
                return 1;
            case "medium":
                return 2;
            case "hard":
                return 3;
            default:
                return 1;
        }
    }


}
