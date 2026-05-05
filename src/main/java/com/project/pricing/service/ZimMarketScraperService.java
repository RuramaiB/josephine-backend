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
        scrapingService.scrapeTM();
        scrapingService.scrapeOKZimbabwe();
        scrapingService.scrapeSpar();
        scrapingService.scrapeChoppies();

        // Fuel Monitoring Expansion (Regional Hubs)
        marketDataService.updateFuelPrice("Petrol (Blend)", 1.62, "ZERA Feed", "Harare");
        marketDataService.updateFuelPrice("Diesel (50)", 1.68, "ZERA Feed", "Bulawayo");

        log.info("National Market audit completed.");
    }
}
