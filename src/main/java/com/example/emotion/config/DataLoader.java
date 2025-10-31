package com.example.emotion.config;

import com.example.emotion.domain.EmotionLog;
import com.example.emotion.repository.EmotionLogRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoader implements ApplicationRunner {
    private final EmotionLogRepository repository;

    public DataLoader(EmotionLogRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        // 항상 초기화 후 CSV 로드
        repository.deleteAll();
        ClassPathResource resource = new ClassPathResource("data/emotion_log_2024_2025_hourly.csv");
        if (!resource.exists()) return;

        List<EmotionLog> batch = new ArrayList<>();
        ZoneId seoul = ZoneId.of("Asia/Seoul");
        DateTimeFormatter legacy = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; if (line.toLowerCase().contains("created") || line.toLowerCase().contains("level")) continue; }
                String[] parts = line.trim().split(",");
                if (parts.length < 2) continue;
                // 지원 포맷: level,created_at 혹은 created_at,level
                String a = parts[0].trim().replace("\"", "");
                String b = parts[1].trim().replace("\"", "");
                String lvStr; String tsStr;
                if (a.matches("^\\d+$")) { // level first
                    lvStr = a; tsStr = b;
                } else {
                    tsStr = a; lvStr = b;
                }
                try {
                    int level = Integer.parseInt(lvStr);
                    Instant createdAt;
                    // 1) ISO-8601 시도
                    try {
                        createdAt = Instant.parse(tsStr);
                    } catch (Exception isoEx) {
                        // 2) "yyyy-MM-dd HH:mm:ss" (Asia/Seoul 기준) 처리
                        LocalDateTime ldt = LocalDateTime.parse(tsStr, legacy);
                        createdAt = ldt.atZone(seoul).toInstant();
                    }
                    if (level < 1 || level > 5) continue;
                    EmotionLog log = new EmotionLog();
                    log.setLevel(level);
                    log.setCreatedAt(createdAt);
                    batch.add(log);
                } catch (Exception ignored) { }
            }
        }
        if (!batch.isEmpty()) repository.saveAll(batch);
    }
}


