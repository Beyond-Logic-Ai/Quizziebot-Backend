package com.quizzka.backend.payload.request;

import com.quizzka.backend.payload.request.helper.QuestionResponse;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
    private String id;
    private String email;
    @Getter
    private String password;
    private String phoneNumber;
    private String firstname;
    private String lastname;
    private int age;
    private String country;
    private List<QuestionResponse> quizResponses;
    private LocalDateTime quizStartTime;
    private LocalDateTime quizEndTime;

}
