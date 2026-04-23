package com.project.pricing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class InsightCacheService {

    private final GwatidzoIntelligenceService intelligenceService;
    private final MarketDataService marketDataService;

    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    private static final String INSIGHT_KEY = "market_insight";
    private static final String LAST_UPDATED_KEY = "last_updated";

    public Map<String, String> getLatestInsight() {
        if (!cache.containsKey(INSIGHT_KEY)) {
            // Initial load if empty
            updateInsightSync();
        }
        return Map.of(
            "insight", (String) cache.getOrDefault(INSIGHT_KEY, "Collecting market intelligence..."),
            "lastUpdated", (String) cache.getOrDefault(LAST_UPDATED_KEY, "N/A")
        );
    }

    @Scheduled(fixedRate = 14400000) // Every 4 hours
    public void scheduledUpdate() {
        log.info("Starting scheduled background market insight update...");
        updateInsightAsync();
    }

    @Async
    public void updateInsightAsync() {
        try {
            String insight = generateInsight();
            cache.put(INSIGHT_KEY, insight);
            cache.put(LAST_UPDATED_KEY, LocalDateTime.now().toString());
            log.info("Background market insight updated successfully.");
        } catch (Exception e) {
            log.error("Failed to update market insight asynchronously: {}", e.getMessage());
        }
    }

    private void updateInsightSync() {
        try {
            String insight = "Market remains stable. Detailed AI analysis is being generated of historical trends and regional disparities.";
            // We set a placeholder first to avoid blocking
            cache.putIfAbsent(INSIGHT_KEY, insight);
            cache.putIfAbsent(LAST_UPDATED_KEY, LocalDateTime.now().toString());
            // Then trigger real update in background
            updateInsightAsync();
        } catch (Exception e) {
            log.error("Failed to initialize market insight: {}", e.getMessage());
        }
    }

    private String generateInsight() {
        // Collect data summary from MarketDataService
        var trends = marketDataService.getCommodityTrends();
        String summary = trends.stream()
            .map(t -> String.format("%s (%s): %s by %.1f%%", t.get("name"), t.get("category"), t.get("trend"), t.get("changePct")))
            .collect(java.util.stream.Collectors.joining(", "));
        
        return intelligenceService.generateGlobalMarketSummary(summary);
    }
}
