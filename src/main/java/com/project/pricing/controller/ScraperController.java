package com.project.pricing.controller;

import com.project.pricing.service.ZimMarketScraperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/scraper")
@RequiredArgsConstructor
public class ScraperController {
    private final ZimMarketScraperService scraperService;

    @PostMapping("/trigger")
    public ResponseEntity<String> triggerScrape() {
        scraperService.scrapeRetailPrices();
        return ResponseEntity.ok("Gwatidzo market scrape triggered manually.");
    }

    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("Scraper Service is ACTIVE. Next scheduled run: 01:00 AM.");
    }
}
