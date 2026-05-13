package com.project.pricing.controller;

import com.project.pricing.dto.ProductPriceResponse;
import com.project.pricing.model.PriceRecord;
import com.project.pricing.service.MarketDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.pricing.dto.MarketStatsDTO;
import java.util.List;

@RestController
@RequestMapping("/api/v1/market")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MarketDataController {
    private final MarketDataService marketDataService;
    private final com.project.pricing.service.InsightCacheService insightCacheService;
    private final com.project.pricing.service.GwatidzoIntelligenceService intelligenceService;

    @PostMapping("/ai-chat")
    public ResponseEntity<java.util.Map<String, String>> aiChat(@RequestBody java.util.Map<String, String> request) {
        String query = request.get("query");
        String context = marketDataService.getMarketSummaryContext();
        String response = intelligenceService.queryMarketAI(query, context);
        return ResponseEntity.ok(java.util.Map.of("response", response));
    }

    @PostMapping("/purge")
    public ResponseEntity<String> purgeData() {
        marketDataService.purgeNonEssentialData();
        return ResponseEntity.ok("Non-essential data purged successfully.");
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductPriceResponse>> getProductsByCategory(
            @RequestParam String category) {
        return ResponseEntity.ok(marketDataService.getProductsWithPrices(category));
    }

    @GetMapping("/price-history/{productId}")
    public ResponseEntity<List<PriceRecord>> getPriceHistory(@PathVariable String productId) {
        return ResponseEntity.ok(marketDataService.getPriceHistory(productId));
    }

    @GetMapping("/stats")
    public ResponseEntity<MarketStatsDTO> getStats() {
        return ResponseEntity.ok(marketDataService.getStats());
    }

    @GetMapping("/high-margins")
    public ResponseEntity<List<ProductPriceResponse>> getHighMargins() {
        return ResponseEntity.ok(marketDataService.getHighMarginCommodities());
    }

    @GetMapping("/sources")
    public ResponseEntity<List<java.util.Map<String, Object>>> getSources() {
        return ResponseEntity.ok(marketDataService.getDataSourceStatus());
    }

    @GetMapping("/indices")
    public ResponseEntity<List<java.util.Map<String, Object>>> getRegionalIndices() {
        return ResponseEntity.ok(marketDataService.getRegionalIndices());
    }

    @GetMapping("/ai-insights")
    public ResponseEntity<java.util.Map<String, String>> getAIInsights() {
        return ResponseEntity.ok(insightCacheService.getLatestInsight());
    }

    @PostMapping("/ai-insights/refresh")
    public ResponseEntity<String> refreshAIInsights() {
        insightCacheService.updateInsightAsync();
        return ResponseEntity.ok("AI Insight refresh triggered in background.");
    }

    @GetMapping("/stability")
    public ResponseEntity<List<java.util.Map<String, Object>>> getStabilityTrends() {
        return ResponseEntity.ok(marketDataService.getUnstableProducts());
    }

    @GetMapping("/trends")
    public ResponseEntity<List<java.util.Map<String, Object>>> getTrends() {
        return ResponseEntity.ok(marketDataService.getCommodityTrends());
    }

    /**
     * Endpoint to manually submit a price point (Used by field officers)
     */
    @PostMapping("/report")
    public ResponseEntity<String> reportPrice(
            @RequestParam String productName,
            @RequestParam String brand,
            @RequestParam String unit,
            @RequestParam double price,
            @RequestParam String retailer,
            @RequestParam String region) {
        marketDataService.trackProductPrice(productName, brand, "RETAIL", unit, price, retailer, region, null);
        return ResponseEntity.ok("Price reported successfully for " + productName);
    }

    /**
     * Specialized endpoint for fuel prices
     */
    @PostMapping("/fuel/update")
    public ResponseEntity<String> updateFuel(
            @RequestParam String fuelType,
            @RequestParam double price,
            @RequestParam String station,
            @RequestParam String region) {
        marketDataService.updateFuelPrice(fuelType, price, station, region);
        return ResponseEntity.ok("Fuel price updated for " + fuelType);
    }
}
