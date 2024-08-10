package com.quizzka.backend.payload.request;

import lombok.Data;

@Data
public class GoogleSignInRequest {
    private String idToken;
}
