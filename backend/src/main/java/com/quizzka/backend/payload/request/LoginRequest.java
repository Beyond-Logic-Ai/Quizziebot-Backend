package com.quizzka.backend.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class LoginRequest {
    private String identifier; // This can be either email or username
    private String password;
}
