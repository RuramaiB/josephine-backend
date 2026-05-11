package com.project.pricing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ZimMarketScraperService {
    private final MarketDataService marketDataService;
    private final ScrapingService scrapingService;

    /**
     * National Scraper for Zimbabwean retailers.
     * Uses Jsoup via ScrapingService to extract real-time prices.
     */
    @Scheduled(cron = "0 0 1 * * *") // Run daily at 1 AM
    public void scrapeRetailPrices() {
        log.info("Starting Gwatidzo automated national market audit...");

        // National Retailers (Generic Jsoup implementation)
        scrapingService.scrapeAll();

        // Fuel Monitoring is handled via scrapingService.scrapeZERA() called in scrapeAll()

        log.info("National Market audit completed.");
    }
}
