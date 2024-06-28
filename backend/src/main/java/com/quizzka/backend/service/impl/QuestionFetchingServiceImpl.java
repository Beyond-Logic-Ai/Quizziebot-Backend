package com.quizzka.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizzka.backend.entity.Category;
import com.quizzka.backend.entity.Question;
import com.quizzka.backend.entity.QuestionCollection;
import com.quizzka.backend.service.CategoryService;
import com.quizzka.backend.service.QuestionFetchingService;
import com.quizzka.backend.service.QuestionService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class QuestionFetchingServiceImpl implements QuestionFetchingService {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RestTemplate restTemplate;

//    @Scheduled(cron = "0 0 * * * ?") // This cron expression runs the job every hour
    public void fetchQuestionsFromLLM() {
        List<Category> categories = categoryService.getAllCategories();

        for (Category category : categories) {
            String categoryName = category.getName();
            try {
                List<Question> questions = fetchQuestionsFromApi(categoryName);
                for (Question question : questions) {
                    question.setQuestionId(UUID.randomUUID().toString());
                }

                QuestionCollection questionCollection = new QuestionCollection();
                questionCollection.setCategory(categoryName);
                questionCollection.setQuestions(questions);
                questionCollection.setCreatedAt(new Date());
                questionCollection.setUpdatedAt(new Date());
                questionService.saveQuestions(questionCollection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private List<Question> fetchQuestionsFromApi(String category) throws Exception {
        String prompt = "Generate 10 quiz questions in the category of " + category + ". Each question should be structured in JSON format with the following fields:" +
                "- questionId: A unique identifier for the question." +
                "- questionText: The text of the quiz question." +
                "- options: An array of four possible answers." +
                "- correctOption: The correct answer from the options array." +
                "- difficulty: The difficulty level of the question (3-easy, 3-medium, 3-hard, 1-your choice)." +
                "- timeLimit: The time limit in seconds for answering the question." +
                "Please provide the JSON output with the specified structure.";

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
            question.setTimeLimit(questionObject.getInt("timeLimit"));

            questions.add(question);
        }
        return questions;
    }
}
