package com.learnip.logger.controller;


import java.net.URI;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.learnip.logger.entity.ClickLog;
import com.learnip.logger.model.UrlMapping;
import com.learnip.logger.repository.ClickLogRepository;
import com.learnip.logger.repository.UrlMappingRepository;
import com.learnip.logger.service.UrlService;

import jakarta.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/url")
public class UrlController {

    @Autowired
    private UrlService urlService;

    @Autowired
    private UrlMappingRepository urlMappingRepository;

    @Autowired
    private ClickLogRepository clickLogRepository;

    @GetMapping("/{shortenedUrl}")
    public ResponseEntity<String> redirect(@PathVariable String shortenedUrl, HttpServletRequest request) {
        UrlMapping urlMapping = urlMappingRepository.findByShortenedUrl(shortenedUrl);
        if (urlMapping != null) {
            // Log the click with IP and user agent
            logClick(request, shortenedUrl);
            // Return the original URL as response body
            return ResponseEntity.ok(urlMapping.getOriginalUrl());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Handle not found case
    }

    private void logClick(HttpServletRequest request, String shortenedUrl) {
        ClickLog clickLog = new ClickLog();
        clickLog.setShortenedUrl(shortenedUrl);
        clickLog.setIpAddress(request.getRemoteAddr());
        clickLog.setUserAgent(request.getHeader("User-Agent"));
        clickLog.setTimestamp(LocalDateTime.now());
        clickLog.setLatitude(0.0); // Set to actual GPS data if available
        clickLog.setLongitude(0.0); // Set to actual GPS data if available
        clickLogRepository.save(clickLog);
    }

    @PostMapping("/shorten")
    @ResponseBody
    public UrlMapping shortenUrl(@RequestParam String originalUrl,
                                  @RequestParam(required = false) Double lat,
                                  @RequestParam(required = false) Double lng) {
        return urlService.shortenUrl(originalUrl);
    }

    @GetMapping("/fetch-url/{shortenedUrl}")
    @ResponseBody
    public String fetchUrl(@PathVariable String shortenedUrl) {
        return urlService.getUrlContent(shortenedUrl);
    }

    // Other methods for logging, etc.
}
