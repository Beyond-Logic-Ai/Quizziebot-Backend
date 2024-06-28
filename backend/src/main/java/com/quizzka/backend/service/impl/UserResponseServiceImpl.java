package com.quizzka.backend.service.impl;

import com.quizzka.backend.entity.QuizQuestion;
import com.quizzka.backend.entity.UserResponse;
import com.quizzka.backend.payload.request.helper.QuestionResponseOld;
import com.quizzka.backend.repository.QuizQuestionRepository;
import com.quizzka.backend.repository.UserResponseRepository;
import com.quizzka.backend.service.UserResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserResponseServiceImpl implements UserResponseService {

    @Autowired
    private UserResponseRepository userResponseRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private LeagueService leagueService;

    public void saveUserResponse(String userId, List<QuestionResponseOld> responses, LocalDateTime startTime, LocalDateTime endTime) {
        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(userId);
        userResponse.setResponses(responses);
        userResponse.setQuizStartTime(startTime);
        userResponse.setQuizEndTime(endTime);
        if (startTime != null && endTime != null) {
            userResponse.setTimeTaken(java.time.Duration.between(startTime, endTime).getSeconds());
        } else {
            userResponse.setTimeTaken(0);
        }

        int totalQuestions = responses.size();
        int correctAnswers = calculateCorrectAnswers(responses);
        double percentage = ((double) correctAnswers / totalQuestions) * 100;
        int xpEarned = correctAnswers;

        userResponse.setPercentage(percentage);
        userResponse.setXpEarned(xpEarned);

        userResponseRepository.save(userResponse);
        leagueService.updateXp(userId, xpEarned);
    }

    private int calculateCorrectAnswers(List<QuestionResponseOld> responses) {
        int correctAnswers = 0;
        for (QuestionResponseOld response : responses) {
            QuizQuestion question = quizQuestionRepository.findById(response.getQuestionId()).orElse(null);
            if (question != null && question.getAnswer().equals(response.getAnswer())) {
                correctAnswers++;
            }
        }
        return correctAnswers;
    }

    @Override
    public UserResponse getLastUserResponse(String userId) {
        return userResponseRepository.findTopByUserIdOrderByQuizEndTimeDesc(userId).orElse(null);
    }
}
