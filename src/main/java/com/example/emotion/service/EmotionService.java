package com.example.emotion.service;

import com.example.emotion.domain.EmotionLog;
import com.example.emotion.dto.EmotionRes;
import com.example.emotion.dto.StatRes;
import com.example.emotion.exception.BadRequestException;
import com.example.emotion.repository.EmotionLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmotionService {
    private final EmotionLogRepository repository;

    public EmotionService(EmotionLogRepository repository) {
        this.repository = repository;
    }

    public EmotionRes create(int level) {
        validateLevel(level);
        EmotionLog log = new EmotionLog();
        log.setLevel(level);
        EmotionLog saved = repository.save(log);
        return new EmotionRes(saved.getId(), saved.getLevel(), saved.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public List<EmotionRes> list(Instant from, Instant to) {
        Instant start = Optional.ofNullable(from).orElse(Instant.EPOCH);
        Instant end = Optional.ofNullable(to).orElse(Instant.now());
        return repository.findByCreatedAtBetweenOrderByCreatedAtDesc(start, end)
                .stream()
                .map(e -> new EmotionRes(e.getId(), e.getLevel(), e.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public EmotionRes update(long id, int level) {
        validateLevel(level);
        EmotionLog log = repository.findById(id)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 감정 로그입니다."));
        log.setLevel(level);
        EmotionLog saved = repository.save(log);
        return new EmotionRes(saved.getId(), saved.getLevel(), saved.getCreatedAt());
    }

    public void delete(long id) {
        if (!repository.existsById(id)) {
            throw new BadRequestException("존재하지 않는 감정 로그입니다.");
        }
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<StatRes> stats(String range) {
        ZoneId seoul = ZoneId.of("Asia/Seoul");
        ZonedDateTime now = ZonedDateTime.now(seoul);

        if ("daily".equals(range)) {
            ZonedDateTime start = now.toLocalDate().atStartOfDay(seoul);
            ZonedDateTime end = start.plusDays(1);
            return bucketHourly(start, end);
        }
        if ("weekly".equals(range)) {
            ZonedDateTime start = now.with(java.time.DayOfWeek.MONDAY).toLocalDate().atStartOfDay(seoul);
            if (now.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
                // HACK: with(MONDAY) on Sunday goes to next Monday; move back 6 days
                start = now.minusDays(6).toLocalDate().atStartOfDay(seoul);
            }
            ZonedDateTime end = start.plusDays(7);
            return bucketWeekdays(start, end);
        }
        if ("monthly".equals(range)) {
            ZonedDateTime start = now.withDayOfMonth(1).toLocalDate().atStartOfDay(seoul);
            ZonedDateTime end = start.plusMonths(1);
            return bucketMonthDays(start, end);
        }
        if ("yearly".equals(range)) {
            ZonedDateTime start = now.withDayOfYear(1).toLocalDate().atStartOfDay(seoul);
            ZonedDateTime end = start.plusYears(1);
            return bucketMonths(start, end);
        }
        throw new BadRequestException("range는 daily|weekly|monthly|yearly 중 하나여야 합니다.");
    }

    private List<StatRes> bucketHourly(ZonedDateTime start, ZonedDateTime end) {
        List<EmotionLog> logs = repository.findByCreatedAtBetweenOrderByCreatedAtDesc(start.toInstant(), end.toInstant());
        ZoneId seoul = start.getZone();
        LinkedHashMap<Integer, List<Integer>> buckets = new LinkedHashMap<>();
        for (int h=0; h<24; h++) buckets.put(h, new ArrayList<>());
        for (EmotionLog e : logs) {
            int hour = e.getCreatedAt().atZone(seoul).getHour();
            buckets.get(hour).add(e.getLevel());
        }
        return buckets.entrySet().stream()
                .map(ent -> toStat(String.format("%02d", ent.getKey()), ent.getValue()))
                .collect(Collectors.toList());
    }

    private List<StatRes> bucketWeekdays(ZonedDateTime start, ZonedDateTime end) {
        List<EmotionLog> logs = repository.findByCreatedAtBetweenOrderByCreatedAtDesc(start.toInstant(), end.toInstant());
        ZoneId seoul = start.getZone();
        String[] labels = {"월","화","수","목","금","토","일"};
        LinkedHashMap<Integer, List<Integer>> buckets = new LinkedHashMap<>();
        for (int i=0;i<7;i++) buckets.put(i, new ArrayList<>());
        for (EmotionLog e : logs) {
            int dow = e.getCreatedAt().atZone(seoul).getDayOfWeek().getValue(); // 1=Mon..7=Sun
            int idx = (dow-1);
            buckets.get(idx).add(e.getLevel());
        }
        return buckets.entrySet().stream()
                .map(ent -> toStat(labels[ent.getKey()], ent.getValue()))
                .collect(Collectors.toList());
    }

    private List<StatRes> bucketMonthDays(ZonedDateTime start, ZonedDateTime end) {
        List<EmotionLog> logs = repository.findByCreatedAtBetweenOrderByCreatedAtDesc(start.toInstant(), end.toInstant());
        ZoneId seoul = start.getZone();
        int last = start.toLocalDate().lengthOfMonth();
        LinkedHashMap<Integer, List<Integer>> buckets = new LinkedHashMap<>();
        for (int d=1; d<=last; d++) buckets.put(d, new ArrayList<>());
        for (EmotionLog e : logs) {
            int day = e.getCreatedAt().atZone(seoul).getDayOfMonth();
            buckets.get(day).add(e.getLevel());
        }
        return buckets.entrySet().stream()
                .map(ent -> toStat(String.format("%02d", ent.getKey()), ent.getValue()))
                .collect(Collectors.toList());
    }

    private List<StatRes> bucketMonths(ZonedDateTime start, ZonedDateTime end) {
        List<EmotionLog> logs = repository.findByCreatedAtBetweenOrderByCreatedAtDesc(start.toInstant(), end.toInstant());
        ZoneId seoul = start.getZone();
        LinkedHashMap<Integer, List<Integer>> buckets = new LinkedHashMap<>();
        for (int m=1; m<=12; m++) buckets.put(m, new ArrayList<>());
        for (EmotionLog e : logs) {
            int month = e.getCreatedAt().atZone(seoul).getMonthValue();
            buckets.get(month).add(e.getLevel());
        }
        return buckets.entrySet().stream()
                .map(ent -> toStat(String.format("%02d", ent.getKey()), ent.getValue()))
                .collect(Collectors.toList());
    }

    private StatRes toStat(String label, List<Integer> levels) {
        long count = levels.size();
        double avg = count == 0 ? 0.0 : levels.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        return new StatRes(label, count, avg);
    }

    private void validateLevel(int level) {
        if (level < 1 || level > 5) {
            throw new BadRequestException("level은 1..5 범위여야 합니다.");
        }
    }
}


