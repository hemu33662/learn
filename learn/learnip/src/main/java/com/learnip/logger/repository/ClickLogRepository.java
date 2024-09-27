package com.learnip.logger.repository;


import com.learnip.logger.entity.ClickLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClickLogRepository extends JpaRepository<ClickLog, Long> {
    List<ClickLog> findByShortenedUrl(String shortenedUrl);
}

