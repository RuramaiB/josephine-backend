package com.project.pricing.controller;

import com.project.pricing.dto.ProductPriceResponse;
import com.project.pricing.model.PriceRecord;
import com.project.pricing.repository.PriceRecordRepository;
import com.project.pricing.service.MarketDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@org.springframework.web.bind.annotation.CrossOrigin(origins = "*")
public class PublicPriceController {

    private final MarketDataService marketDataService;
    private final PriceRecordRepository priceRecordRepository;

    @GetMapping("/prices")
    public List<ProductPriceResponse> getLatestPrices(@RequestParam(required = false) String category) {
        return marketDataService.getProductsWithPrices(category);
    }

    @GetMapping("/stats")
    public Map<String, Object> getMarketStats() {
        List<PriceRecord> records = priceRecordRepository.findAll();
        long alertsCount = records.stream().filter(PriceRecord::isAlert).count();
        double avgRisk = records.stream().mapToDouble(PriceRecord::getRiskScore).average().orElse(0.0);

        Map<String, List<PriceRecord>> regionalGroup = records.stream()
            .filter(r -> r.getRegion() != null)
            .collect(Collectors.groupingBy(PriceRecord::getRegion));

        Map<String, Object> regionalStats = regionalGroup.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> Map.of(
                    "avgPrice", e.getValue().stream().mapToDouble(PriceRecord::getPrice).average().orElse(0.0),
                    "alerts", e.getValue().stream().filter(PriceRecord::isAlert).count()
                )
            ));

        return Map.of(
                "totalRecords", records.size(),
                "activeAlerts", alertsCount,
                "averageMarketRisk", avgRisk,
                "sourcesCount", records.stream().map(PriceRecord::getSource).distinct().count(),
                "regionalBreakdown", regionalStats,
                "lastUpdate", records.isEmpty() ? "N/A" : records.get(records.size() - 1).getTimestamp().toString());
    }

    @GetMapping("/categories")
    public List<String> getCategories() {
        return priceRecordRepository.findAll().stream()
                .filter(r -> r.getSource() != null && !"Numbeo".equalsIgnoreCase(r.getSource()))
                .filter(r -> !("FUEL".equalsIgnoreCase(r.getCategory()) && !"ZERA".equalsIgnoreCase(r.getSource())))
                .map(PriceRecord::getCategory)
                .filter(c -> c != null && !c.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    @GetMapping("/compare")
    public List<Map<String, Object>> compareSources(
            @RequestParam String sourceA, 
            @RequestParam String sourceB) {
        return marketDataService.getComparison(sourceA, sourceB);
    }

    @GetMapping("/sources")
    public List<String> getSources() {
        return marketDataService.getDataSourceStatus().stream()
            .map(m -> m.get("name").toString().replace(" Scraper", "")).toList();
    }
}
