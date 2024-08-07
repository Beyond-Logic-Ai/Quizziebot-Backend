package com.quizzka.backend.service.impl;

import com.quizzka.backend.entity.Question;
import com.quizzka.backend.service.AIService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AIServiceImpl implements AIService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public String fetchBriefFromAI(String topic) throws Exception {
        String prompt = "Provide a brief overview of the topic: " + topic;
        String encodedPrompt = URLEncoder.encode(prompt, StandardCharsets.UTF_8);
        String url = apiUrl + "/prompt?prompt=" + encodedPrompt + "&geminiKey=" + apiKey;
        String response = restTemplate.getForObject(url, String.class);

        // Extract the "text" field from the JSON response
        return extractTextFromResponse(response);
    }

    public List<Question> fetchQuestionsFromAI(String topic) throws Exception {
        String prompt = "Generate 10 quiz questions in the topic of " + topic + ". Each question should be structured in JSON format with the following fields:\n" +
                "- questionId: A unique identifier for the question.\n" +
                "- questionText: The text of the quiz question.\n" +
                "- options: An array of four possible answers.\n" +
                "- correctOption: The correct answer from the options array.\n" +
                "- difficulty: The difficulty level of the question (easy, medium, hard).\n" +
                "- timeLimit: The time limit for answering each question should be 10 seconds.\n" +
                "Please provide a balanced mix of easy, medium, and hard questions.";

        String encodedPrompt = URLEncoder.encode(prompt, StandardCharsets.UTF_8);
        String url = apiUrl + "/prompt?prompt=" + encodedPrompt + "&geminiKey=" + apiKey;
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
            question.setQuestionId(UUID.randomUUID().toString());
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

    public String fetchResponseFromAI(String prompt) throws Exception {
        String encodedPrompt = URLEncoder.encode(prompt, StandardCharsets.UTF_8);
        String url = apiUrl + "/prompt?prompt=" + encodedPrompt + "&geminiKey=" + apiKey;
        String response = restTemplate.getForObject(url, String.class);
        return extractTextFromResponse(response);
    }
}
