package com.quizzka.backend.service.impl;

import com.quizzka.backend.entity.*;
import com.quizzka.backend.entity.helper.QuestionStatus;
import com.quizzka.backend.payload.response.QuizGenerationResponse;
import com.quizzka.backend.payload.response.TopicBriefResponse;
import com.quizzka.backend.repository.*;
import com.quizzka.backend.service.AIService;
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

    @Autowired
    private AIService aiService;

    @Autowired
    private CustomQuizSessionRepository customQuizSessionRepository;

    @Autowired
    private CustomQuizCollectionRepository customQuizCollectionRepository;

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
    public Map<String, Object> getQuestions(String userId, String mode, String category, String difficulty) {
        List<Question> selectedQuestions;

        if ("classic".equalsIgnoreCase(mode)) {
            selectedQuestions = getClassicModeQuestions(userId);
        } else if ("arcade".equalsIgnoreCase(mode)) {
            if (category == null || difficulty == null) {
                throw new IllegalArgumentException("Category and difficulty are required for Arcade mode.");
            }
            selectedQuestions = getArcadeModeQuestions(userId, category, difficulty);
        } else {
            throw new IllegalArgumentException("Invalid mode specified. Valid modes are 'classic' and 'arcade'.");
        }

        // Generate a unique quizId
        String quizId = UUID.randomUUID().toString();

        // Save the session with the newly selected question IDs
        List<QuestionStatus> questionStatuses = selectedQuestions.stream()
                .map(question -> new QuestionStatus(question.getQuestionId(), false)) // Mark new questions as unanswered
                .toList();

        QuizSession quizSession = new QuizSession();
        quizSession.setQuizId(quizId);
        quizSession.setUserId(userId);
        quizSession.setQuestionStatuses(questionStatuses);
        quizSession.setMode(mode);
        quizSession.setCreatedAt(new Date());
        quizSessionRepository.save(quizSession);

        Map<String, Object> response = new HashMap<>();
        response.put("quizId", quizId);
        response.put("questions", selectedQuestions);

        return response;
    }

    private List<Question> getClassicModeQuestions(String userId) {
        List<String> answeredQuestionIds = getAnsweredQuestionIds(userId);

        List<Question> easyQuestions = fetchQuestionsByDifficulty("easy", answeredQuestionIds);
        List<Question> mediumQuestions = fetchQuestionsByDifficulty("medium", answeredQuestionIds);
        List<Question> hardQuestions = fetchQuestionsByDifficulty("hard", answeredQuestionIds);

        // Shuffle and limit to the required number of questions
        Collections.shuffle(easyQuestions);
        Collections.shuffle(mediumQuestions);
        Collections.shuffle(hardQuestions);

        List<Question> selectedQuestions = new ArrayList<>();
        selectedQuestions.addAll(easyQuestions.stream().limit(3).toList());
        selectedQuestions.addAll(mediumQuestions.stream().limit(4).toList());
        selectedQuestions.addAll(hardQuestions.stream().limit(3).toList());

        // Shuffle the final list to mix easy, medium, and hard questions
        Collections.shuffle(selectedQuestions);

        return selectedQuestions;
    }

    private List<Question> getArcadeModeQuestions(String userId, String category, String difficulty) {
        List<String> answeredQuestionIds = getAnsweredQuestionIds(userId);

        List<QuestionCollection> collections = questionCollectionRepository.findByCategory(category);
        List<Question> allQuestions = collections.stream()
                .flatMap(collection -> collection.getQuestions().stream())
                .filter(question -> question.getDifficulty().equalsIgnoreCase(difficulty))
                .filter(question -> !answeredQuestionIds.contains(question.getQuestionId()))
                .collect(Collectors.toList());

        // Shuffle and limit to 10 questions
        Collections.shuffle(allQuestions);
        return allQuestions.stream().limit(10).collect(Collectors.toList());
    }

    private List<String> getAnsweredQuestionIds(String userId) {
        List<QuizSession> quizSessions = quizSessionRepository.findByUserId(userId);

        List<String> answeredQuestionIds = quizSessions.stream()
                .flatMap(session -> session.getQuestionStatuses().stream())
                .filter(QuestionStatus::isAnswered)
                .map(QuestionStatus::getQuestionId)
                .distinct()
                .collect(Collectors.toList());

        // Log the answered question IDs
        System.out.println("Answered Question IDs for user " + userId + ": " + answeredQuestionIds);

        return answeredQuestionIds;
    }

    private List<Question> fetchQuestionsByDifficulty(String difficulty, List<String> answeredQuestionIds) {
        List<QuestionCollection> collections = questionCollectionRepository.findAll();
        List<Question> questions = collections.stream()
                .flatMap(collection -> collection.getQuestions().stream())
                .filter(question -> question.getDifficulty().equalsIgnoreCase(difficulty))
                .filter(question -> !answeredQuestionIds.contains(question.getQuestionId()))
                .collect(Collectors.toList());

        // Log the questions being fetched
        System.out.println("Fetched " + difficulty + " questions, excluding answered ones: " +
                questions.stream().map(Question::getQuestionId).collect(Collectors.toList()));

        return questions;
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

    @Override
    public List<Question> getQuestionsByCategoryAsQuestions(String category) {
        List<QuestionCollection> collections = questionCollectionRepository.findByCategory(category);
        return collections.stream()
                .flatMap(collection -> collection.getQuestions().stream())
                .collect(Collectors.toList());
    }

    @Override
    public TopicBriefResponse getBrief(String topic) throws Exception {
        String brief = aiService.fetchBriefFromAI(topic);
        return new TopicBriefResponse(brief);
    }

    public QuizGenerationResponse generateQuiz(String topic, String userId) throws Exception {
        List<Question> questions = aiService.fetchQuestionsFromAI(topic);
        for (Question question : questions) {
            question.setQuestionId(UUID.randomUUID().toString());
        }

        CustomQuizCollection customQuizCollection = new CustomQuizCollection();
        customQuizCollection.setId(UUID.randomUUID().toString());
        customQuizCollection.setCategory(topic);
        customQuizCollection.setQuestions(questions);
        customQuizCollection.setCreatedAt(new Date());
        customQuizCollection.setUpdatedAt(new Date());

        customQuizCollectionRepository.save(customQuizCollection);

        // Generate a unique quizId
        String quizId = UUID.randomUUID().toString();

        // Save the session with the newly selected question IDs
        List<QuestionStatus> questionStatuses = questions.stream()
                .map(question -> new QuestionStatus(question.getQuestionId(), false)) // Mark new questions as unanswered
                .collect(Collectors.toList());

        CustomQuizSession customQuizSession = new CustomQuizSession();
        customQuizSession.setQuizId(quizId);
        customQuizSession.setUserId(userId);
        customQuizSession.setQuestionStatuses(questionStatuses);
        customQuizSession.setMode("custom");
        customQuizSession.setCreatedAt(new Date());
        customQuizSession.setUpdatedAt(new Date());

        customQuizSessionRepository.save(customQuizSession);

        return new QuizGenerationResponse(quizId, questions);
    }
}
