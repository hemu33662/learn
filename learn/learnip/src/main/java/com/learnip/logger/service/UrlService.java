package com.learnip.logger.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.learnip.logger.model.UrlMapping;
import com.learnip.logger.repository.UrlMappingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UrlService {
    private static final Logger logger = LoggerFactory.getLogger(UrlService.class);

    @Autowired
    private UrlMappingRepository urlMappingRepository;

    @Autowired
    private RestTemplate restTemplate; // Inject RestTemplate

    public UrlMapping shortenUrl(String originalUrl) {
        if (originalUrl == null || originalUrl.isEmpty()) {
            logger.error("Original URL cannot be empty");
            throw new IllegalArgumentException("Original URL cannot be empty");
        }

        String shortenedUrl;
        do {
            shortenedUrl = generateShortenedUrl();
        } while (urlMappingRepository.findByShortenedUrl(shortenedUrl) != null);

        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setOriginalUrl(originalUrl);
        urlMapping.setShortenedUrl(shortenedUrl);
        urlMapping.setTotalClicks(0);

        try {
            return urlMappingRepository.save(urlMapping);
        } catch (Exception e) {
            logger.error("Error saving URL mapping: {}", e.getMessage());
            throw new RuntimeException("Failed to shorten URL");
        }
    }

    private String generateShortenedUrl() {
        // Generate a unique shortened URL
        return RandomStringUtils.randomAlphanumeric(6); // Customize as needed
    }

    public String getUrlContent(String shortenedUrl) {
        String ngrokUrl = "https://4e56-115-99-178-34.ngrok-free.app/url/" + shortenedUrl;
        HttpHeaders headers = new HttpHeaders();
        headers.add("ngrok-skip-browser-warning", "1");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(ngrokUrl, HttpMethod.GET, entity, String.class);
        
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            logger.error("Failed to fetch URL content: {}", response.getStatusCode());
            return null; // Or handle the error as needed
        }
    }
}
