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

        return Map.of(
                "totalRecords", records.size(),
                "activeAlerts", alertsCount,
                "averageMarketRisk", avgRisk,
                "sourcesCount", records.stream().map(PriceRecord::getSource).distinct().count());
    }

    @GetMapping("/categories")
    public List<String> getCategories() {
        return priceRecordRepository.findAll().stream()
                .map(PriceRecord::getCategory)
                .filter(c -> c != null && !c.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }
}
