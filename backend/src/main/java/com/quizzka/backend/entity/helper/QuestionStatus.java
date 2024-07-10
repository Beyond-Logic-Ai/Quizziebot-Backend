package com.quizzka.backend.entity.helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class QuestionStatus {
    private String questionId;
    private boolean isAnswered;
}
