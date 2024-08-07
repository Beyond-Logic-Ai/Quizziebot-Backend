package com.quizzka.backend.service.impl;

import com.quizzka.backend.entity.CustomQuizResult;
import com.quizzka.backend.entity.CustomQuizSession;
import com.quizzka.backend.entity.Question;
import com.quizzka.backend.payload.request.CustomQuizSubmissionRequest;
import com.quizzka.backend.payload.request.helper.Answer;
import com.quizzka.backend.repository.CustomQuizResultRepository;
import com.quizzka.backend.repository.CustomQuizSessionRepository;
import com.quizzka.backend.repository.UserRepository;
import com.quizzka.backend.service.CustomQuizService;
import com.quizzka.backend.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CustomQuizServiceImpl implements CustomQuizService {

    @Autowired
    private CustomQuizResultRepository customQuizResultRepository;

    @Autowired
    private CustomQuizSessionRepository customQuizSessionRepository;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public CustomQuizResult saveCustomQuiz(CustomQuizSubmissionRequest submissionRequest) {
        int correctAnswers = 0;
        int wrongAnswers = 0;
        long totalTimeTaken = 0;

        // Try to find an existing quiz session or create a new one
        CustomQuizSession customQuizSession = customQuizSessionRepository.findByQuizId(submissionRequest.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz session not found"));

        Map<String, Question> questionMap = submissionRequest.getAnswers().stream()
                .map(answer -> questionService.findQuestionById(answer.getQuestionId()))
                .collect(Collectors.toMap(Question::getQuestionId, Function.identity()));

        for (Answer answer : submissionRequest.getAnswers()) {
            Question question = questionMap.get(answer.getQuestionId());
            if (question != null && question.getCorrectOption().equals(answer.getSelectedOption())) {
                correctAnswers++;
            } else {
                wrongAnswers++;
            }

            totalTimeTaken += answer.getTimeTaken();

            customQuizSession.getQuestionStatuses().stream()
                    .filter(qs -> qs.getQuestionId().equals(answer.getQuestionId()))
                    .forEach(qs -> qs.setAnswered(true));
        }

        customQuizSessionRepository.save(customQuizSession);

        CustomQuizResult result = new CustomQuizResult();
        result.setUserId(submissionRequest.getUserId());
        result.setQuizId(submissionRequest.getQuizId());
        result.setAnswers(submissionRequest.getAnswers());
        result.setCorrectAnswers(correctAnswers);
        result.setWrongAnswers(wrongAnswers);
        result.setTotalTimeTaken(totalTimeTaken);
        result.setCreatedAt(new Date());

        customQuizResultRepository.save(result);

        return result;
    }
}
