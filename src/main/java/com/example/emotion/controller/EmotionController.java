package com.example.emotion.controller;

import com.example.emotion.dto.EmotionCreateReq;
import com.example.emotion.dto.EmotionRes;
import com.example.emotion.dto.EmotionUpdateReq;
import com.example.emotion.service.EmotionService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/emotions")
public class EmotionController {
    private final EmotionService service;

    public EmotionController(EmotionService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmotionRes create(@Valid @RequestBody EmotionCreateReq req) {
        return service.create(req.getLevel());
    }

    @GetMapping
    public List<EmotionRes> list(
            @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to
    ) {
        return service.list(from, to);
    }

    @PatchMapping("/{id}")
    public EmotionRes update(@PathVariable("id") long id, @Valid @RequestBody EmotionUpdateReq req) {
        return service.update(id, req.getLevel());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") long id) {
        service.delete(id);
    }
}


