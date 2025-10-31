package com.example.emotion.controller;

import com.example.emotion.dto.StatRes;
import com.example.emotion.service.EmotionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
public class StatController {
    private final EmotionService service;

    public StatController(EmotionService service) {
        this.service = service;
    }

    @GetMapping
    public List<StatRes> stats(@RequestParam("range") String range) {
        return service.stats(range);
    }
}


