package com.quizzka.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "chat_sessions")
public class ChatSession {
    @Id
    private String id;
    private String userId;
    private Date startedAt;
    private Date endedAt;
    private List<ChatMessage> messages;
    private String identifiedTopic;
}