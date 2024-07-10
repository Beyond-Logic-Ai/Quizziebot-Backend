package com.quizzka.backend.payload.request;

import com.quizzka.backend.payload.request.helper.QuestionResponseOld;
import com.quizzka.backend.payload.response.QuizResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
    private String id;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}$",
            message = "Password must be at least 8 characters long, contain at least one digit, one lower case letter, one upper case letter, and one special character"
    )
    private String password;

    private String phoneNumber;

    @NotBlank(message = "First name is mandatory")
    private String firstname;

    @NotBlank(message = "Last name is mandatory")
    private String lastname;

    @NotBlank(message = "Username is mandatory")
    private String username;

    @NotBlank(message = "Gender is mandatory")
    private String gender;

    @NotNull(message = "Date of birth is mandatory")
    private LocalDateTime dob;

    @NotBlank(message = "Account type is mandatory")
    private String accountType;

    private int age;

    @NotBlank(message = "Country is mandatory")
    private String country;

    private boolean rememberMe;

    private String loginType;
}