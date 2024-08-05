package com.quizzka.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizzka.backend.entity.Category;
import com.quizzka.backend.entity.Question;
import com.quizzka.backend.entity.QuestionCollection;
import com.quizzka.backend.repository.QuestionRepository;
import com.quizzka.backend.service.CategoryService;
import com.quizzka.backend.service.QuestionFetchingService;
import com.quizzka.backend.service.QuestionService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionFetchingServiceImpl implements QuestionFetchingService {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private JavaMailSender mailSender;

    private static final Logger logger = LoggerFactory.getLogger(QuestionFetchingServiceImpl.class);

    @Scheduled(cron = "0 0/30 * * * ?") // This cron expression runs the job every 30 minutes
    public void fetchQuestionsFromLLM() {

        logger.info("Starting the cron job to fetch questions from LLM");

        List<Category> categories = categoryService.getAllCategories();

        for (Category category : categories) {
            String categoryName = category.getName();
            try {
                logger.info("Fetching questions for category: {}", categoryName);
                List<Question> newQuestions = fetchQuestionsFromApi(categoryName);

                // Set unique questionId for each question
                for (Question question : newQuestions) {
                    question.setQuestionId(UUID.randomUUID().toString());
                }

                // Filter out existing questions
                List<Question> existingQuestions = questionService.getQuestionsByCategoryAsQuestions(categoryName);
                List<Question> uniqueQuestions = filterUniqueQuestions(newQuestions, existingQuestions);

                if (!uniqueQuestions.isEmpty()) {
                    logger.info("Saving {} new unique questions for category: {}", uniqueQuestions.size(), categoryName);
                    QuestionCollection questionCollection = new QuestionCollection();
                    questionCollection.setCategory(categoryName);
                    questionCollection.setQuestions(uniqueQuestions);
                    questionCollection.setCreatedAt(new Date());
                    questionCollection.setUpdatedAt(new Date());
                    questionService.saveQuestions(questionCollection);
                } else {
                    logger.info("No new unique questions found for category: {}", categoryName);
                }
            } catch (Exception e) {
                logger.error("Error fetching questions for category: {}", categoryName, e);
                sendErrorEmail(e, categoryName); // Send an email if there's an error
            }
        }

        logger.info("Finished the cron job to fetch questions from LLM");
    }

    private List<Question> fetchQuestionsFromApi(String category) throws Exception {
        String prompt = "Generate 10 quiz questions in the category of " + category + ". Each question should be structured in JSON format with the following fields:\n" +
                "- questionId: A unique identifier for the question.\n" +
                "- questionText: The text of the quiz question.\n" +
                "- options: An array of four possible answers.\n" +
                "- correctOption: The correct answer from the options array.\n" +
                "- difficulty: The difficulty level of the question (easy, medium, hard).\n" +
                "- timeLimit: The time limit for answering each question should be 10 seconds.\n" +
                "Please provide exactly 3 easy questions, 4 medium questions, and 3 hard questions in the JSON output with the specified structure.";

        String encodedPrompt = URLEncoder.encode(prompt, StandardCharsets.UTF_8);
        String url = "http://localhost:8080/prompt?prompt=" + encodedPrompt + "&geminiKey=AIzaSyBlGN-vrFH1a6dc3Kt6eTrRKjr6JxeBZXI";
        String response = restTemplate.getForObject(url, String.class);

        // Extract the "text" field from the JSON response
        String extractedText = extractTextFromResponse(response);

        // Parse the extracted text into Question objects
        return parseQuestionsFromText(extractedText);
    }

    private String extractTextFromResponse(String response) {
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray candidates = jsonResponse.getJSONArray("candidates");
        JSONObject candidate = candidates.getJSONObject(0);
        JSONObject content = candidate.getJSONObject("content");
        JSONArray parts = content.getJSONArray("parts");
        JSONObject part = parts.getJSONObject(0);
        return part.getString("text");
    }

    private List<Question> parseQuestionsFromText(String text) {
        // Remove Markdown code block markers if present
        if (text.startsWith("```json")) {
            text = text.substring(7);
        }
        if (text.endsWith("```")) {
            text = text.substring(0, text.length() - 3);
        }

        List<Question> questions = new ArrayList<>();
        JSONArray questionsArray = new JSONArray(text);

        for (int i = 0; i < questionsArray.length(); i++) {
            JSONObject questionObject = questionsArray.getJSONObject(i);
            Question question = new Question();
            question.setQuestionText(questionObject.getString("questionText"));

            List<String> optionsList = new ArrayList<>();
            JSONArray optionsArray = questionObject.getJSONArray("options");
            for (int j = 0; j < optionsArray.length(); j++) {
                optionsList.add(optionsArray.getString(j));
            }
            question.setOptions(optionsList);

            question.setCorrectOption(questionObject.getString("correctOption"));
            question.setDifficulty(questionObject.getString("difficulty"));
            question.setTimeLimit(10);

            questions.add(question);
        }
        return questions;
    }

    private List<Question> filterUniqueQuestions(List<Question> newQuestions, List<Question> existingQuestions) {
        Set<String> existingQuestionTexts = existingQuestions.stream()
                .map(Question::getQuestionText)
                .collect(Collectors.toSet());

        return newQuestions.stream()
                .filter(question -> !existingQuestionTexts.contains(question.getQuestionText()))
                .collect(Collectors.toList());
    }

    private void sendErrorEmail(Exception e, String categoryName) {
        String subject = "Error fetching questions for category: " + categoryName;
        String body = "An error occurred while fetching questions for category: " + categoryName + "\n\n" +
                "Error message: " + e.getMessage() + "\n\n" +
                "Stack trace:\n" + Arrays.toString(e.getStackTrace());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("quizzerapp9@gmail.com");
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}