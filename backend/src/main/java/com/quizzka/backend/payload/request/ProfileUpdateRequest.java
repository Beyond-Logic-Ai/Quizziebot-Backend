package com.quizzka.backend.payload.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.Optional;

@Data
public class ProfileUpdateRequest {
    private String userId;
    private Optional<String> username = Optional.empty();
    private Optional<String> firstname = Optional.empty();
    private Optional<String> lastname = Optional.empty();
    private Optional<String> email = Optional.empty();
    private Optional<LocalDate> dob = Optional.empty();
}
