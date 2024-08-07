package com.quizzka.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    private String sender; // "user" or "gemini"
    private String message;
    private Date timestamp;
}
