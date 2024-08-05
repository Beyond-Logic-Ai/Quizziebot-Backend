package com.quizzka.backend.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class HomeScreenResponse {
    private String userId;
    private String username;
    private String profilePictureUrl;
    private int xp;
    private int coins;
    private String country;
    private Date lastLogin;
}
