package com.quizzka.backend.payload.request;

import lombok.Data;

@Data
public class ValidateOtpRequest {
    private String email;
    private String otp;
}
