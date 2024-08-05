package com.quizzka.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Document(collection = "user_play_stats")
public class UserPlayStats {
    @Id
    private String id;
    private String userId;
    private String userName;
    private int classicPlays;
    private int arcadePlays;
    private int streak;
    private int topPositions;
    private int challengePassed;
    private int fastestRecord;
    private double overallIq;
    private int totalPlays;
    private long totalTimeSpent;
    private Date lastPlayedDate;
}
