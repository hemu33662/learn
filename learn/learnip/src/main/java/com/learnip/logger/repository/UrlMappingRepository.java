package com.learnip.logger.repository;


import com.learnip.logger.model.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {
    UrlMapping findByShortenedUrl(String shortenedUrl);
}
