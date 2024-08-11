package com.quizzka.backend.service;

import com.quizzka.backend.payload.response.LeaderboardEntry;

import java.util.List;

public interface LeaderboardService {
    List<LeaderboardEntry> getGlobalLeaderboard();
    List<LeaderboardEntry> getCountryLeaderboard(String country);
    LeaderboardEntry getUserRank(String userId);
    String getCurrentLeague(int xp);
    LeaderboardEntry getPreviousRank(String userId);
}
