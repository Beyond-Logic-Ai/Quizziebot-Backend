package com.quizzka.backend.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardEntry {
    private String userId;
    private String username;
    private int xp;
    private int score;
    private int rank;
    private String league;
}
