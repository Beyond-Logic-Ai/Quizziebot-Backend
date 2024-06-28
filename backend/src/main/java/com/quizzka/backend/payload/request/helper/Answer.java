package com.quizzka.backend.payload.request.helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Answer {
    private String questionId;
    private String selectedOption;
    private int timeTaken;

}
