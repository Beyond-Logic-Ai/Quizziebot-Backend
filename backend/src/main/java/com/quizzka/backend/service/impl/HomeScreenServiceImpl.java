package com.quizzka.backend.service.impl;

import com.quizzka.backend.entity.HomeScreen;
import com.quizzka.backend.repository.HomeScreenRepository;
import com.quizzka.backend.service.HomeScreenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HomeScreenServiceImpl implements HomeScreenService {

    @Autowired
    private HomeScreenRepository homeScreenRepository;

    @Override
    public HomeScreen getHomeScreen() {
        return homeScreenRepository.findById("homeScreen").orElse(null);
    }
}
