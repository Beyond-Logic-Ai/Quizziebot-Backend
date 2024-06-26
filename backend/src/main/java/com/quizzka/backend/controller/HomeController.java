package com.quizzka.backend.controller;

import com.quizzka.backend.entity.HomeScreen;
import com.quizzka.backend.service.HomeScreenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @Autowired
    private HomeScreenService homeScreenService;

    @GetMapping("/home")
    public HomeScreen getHomeScreen(){
        return homeScreenService.getHomeScreen();
    }
}
