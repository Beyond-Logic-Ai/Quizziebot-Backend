package com.quizzka.backend.payload.request.helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class QuestionResponse {
    private String questionId;
    private String answer;
}
