package com.quizzka.backend.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ErrorResponse {
    private String message;
    private String details;
    private Date timestamp;

    public ErrorResponse(String message, String details) {
        super();
        this.message = message;
        this.details = details;
        this.timestamp = new Date();
    }
}
