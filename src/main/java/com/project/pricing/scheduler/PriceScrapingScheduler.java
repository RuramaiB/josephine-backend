package com.project.pricing.scheduler;

import com.project.pricing.service.ScrapingService;
import com.project.pricing.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceScrapingScheduler {

    private final ScrapingService scrapingService;
    private final AnalysisService analysisService;

    // Run every day at 1 AM
    @Scheduled(cron = "0 0 1 * * ?")
    public void runDailyScraping() {
        log.info("Starting scheduled daily scraping task...");
        scrapingService.scrapeAll();
        analysisService.analyzeRecentPrices();
        log.info("Scheduled daily scraping and analysis completed.");
    }
}
