package com.example.emotion.repository;

import com.example.emotion.domain.EmotionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface EmotionLogRepository extends JpaRepository<EmotionLog, Long> {
    List<EmotionLog> findByCreatedAtBetweenOrderByCreatedAtDesc(Instant from, Instant to);

    @Query(value = "SELECT FORMATDATETIME(created_at, 'yyyy-MM-dd') AS label, COUNT(*) AS cnt, AVG(level) AS avg_val " +
            "FROM emotion_log GROUP BY FORMATDATETIME(created_at, 'yyyy-MM-dd') ORDER BY label", nativeQuery = true)
    List<Object[]> statsDaily();

    @Query(value = "SELECT FORMATDATETIME(created_at, 'YYYY-ww') AS label, COUNT(*) AS cnt, AVG(level) AS avg_val " +
            "FROM emotion_log GROUP BY FORMATDATETIME(created_at, 'YYYY-ww') ORDER BY label", nativeQuery = true)
    List<Object[]> statsWeekly();

    @Query(value = "SELECT FORMATDATETIME(created_at, 'yyyy-MM') AS label, COUNT(*) AS cnt, AVG(level) AS avg_val " +
            "FROM emotion_log GROUP BY FORMATDATETIME(created_at, 'yyyy-MM') ORDER BY label", nativeQuery = true)
    List<Object[]> statsMonthly();

    @Query(value = "SELECT FORMATDATETIME(created_at, 'yyyy') AS label, COUNT(*) AS cnt, AVG(level) AS avg_val " +
            "FROM emotion_log GROUP BY FORMATDATETIME(created_at, 'yyyy') ORDER BY label", nativeQuery = true)
    List<Object[]> statsYearly();
}


