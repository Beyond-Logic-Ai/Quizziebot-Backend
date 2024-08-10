package com.quizzka.backend.service.impl;

import com.quizzka.backend.entity.QuizQuestion;
import com.quizzka.backend.repository.QuizQuestionRepository;
import com.quizzka.backend.service.QuizQuestionService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuizQuestionServiceImpl implements QuizQuestionService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;


    @Override
    public void fetchAndSaveQuiz(String topic) {
        try {
            String prompt = "Generate 5 multiple-choice quiz questions with 4 options each on " + topic +
                    ". I want them in a JSON format. Your response must be only a JSON format. " +
                    "Example: [" +
                    "  {" +
                    "    \"question\": \"The Directive Principles of State Policy as enshrined in the Indian Constitution are inspired by the Constitution of which country?\"," +
                    "    \"options\": {" +
                    "      \"a\": \"USA\"," +
                    "      \"b\": \"Ireland\"," +
                    "      \"c\": \"UK\"," +
                    "      \"d\": \"Germany\"" +
                    "    }," +
                    "    \"answer\": \"b\"" +
                    "  }" +
                    "]";
            String encodedPrompt = URLEncoder.encode(prompt, StandardCharsets.UTF_8);
            String url = "http://localhost:8080/prompt?prompt=" + encodedPrompt + "&geminiKey=AIzaSyBKPybSyqXhIHUsnR8RCpOXhYupuBREM3w";
            String response = restTemplate.getForObject(url, String.class);

            // Extracting the text from the JSON response
            List<QuizQuestion> quizQuestions = extractQuestionsFromResponse(response, topic);
            quizQuestionRepository.saveAll(quizQuestions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<QuizQuestion> extractQuestionsFromResponse(String response, String topic) {
        List<QuizQuestion> quizQuestions = new ArrayList<>();
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray candidates = jsonResponse.getJSONArray("candidates");
        JSONObject candidate = candidates.getJSONObject(0);
        JSONObject content = candidate.getJSONObject("content");
        JSONArray parts = content.getJSONArray("parts");
        JSONObject part = parts.getJSONObject(0);
        String text = part.getString("text");

        JSONArray questionsArray = new JSONArray(text);
        for (int i = 0; i < questionsArray.length(); i++) {
            JSONObject questionObject = questionsArray.getJSONObject(i);
            QuizQuestion quizQuestion = new QuizQuestion();
            quizQuestion.setQuestion(questionObject.getString("question"));
            JSONObject optionsObject = questionObject.getJSONObject("options");
            Map<String, String> optionsMap = new HashMap<>();
            for (String key : optionsObject.keySet()) {
                optionsMap.put(key, optionsObject.getString(key));
            }
            quizQuestion.setOptions(optionsMap);
            quizQuestion.setAnswer(questionObject.getString("answer"));
            quizQuestion.setTopic(topic);
            quizQuestions.add(quizQuestion);
        }
        return quizQuestions;
    }

    @Override
    public List<QuizQuestion> getQuestionsByTopic(String topic) {
        return quizQuestionRepository.findTop15ByTopic(topic);
    }
}
