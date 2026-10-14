package com.gamehub.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameHubController {

    @GetMapping("/")
    public String home() {
        return "🎮 Welcome to Game Hub!";
    }
}