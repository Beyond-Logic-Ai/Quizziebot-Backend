package com.quizzka.backend.controller;

import com.quizzka.backend.entity.UserResponse;
import com.quizzka.backend.payload.request.UserResponseRequest;
import com.quizzka.backend.service.UserResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/quiz")
public class QuizController {

    @Autowired
    private UserResponseService userResponseService;

    @PostMapping("/save-responses")
    public UserResponse saveResponses(@RequestBody UserResponseRequest userResponseRequest) {
        userResponseService.saveUserResponse(
                userResponseRequest.getUserId(),
                userResponseRequest.getResponses(),
                userResponseRequest.getQuizStartTime(),
                userResponseRequest.getQuizEndTime()
        );
        return userResponseService.getLastUserResponse(userResponseRequest.getUserId());
    }
}
