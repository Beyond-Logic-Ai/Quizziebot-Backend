package com.quizzka.backend.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private LocalDate dob;
    private String newJwtToken;
    private String profilePictureUrl;
}