package com.quizzka.backend.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private LocalDate dob;
    private int classicGamesPlayed;
    private int totalPlays;
    private long totalTimeSpent;
    private double rank;
    private int totalXp;
    private int coins;
    private double overallIq;
    private List<IQDataPoint> iqGraph;
    private Achievements achievements;

    @Data
    public static class IQDataPoint {
        private String date;
        private int iq;
    }

    @Data
    public static class Achievements {
        private int streak;
        private int quizPassed;
        private int topPositions;
        private int challengePassed;
        private int fastestRecord;
    }
}