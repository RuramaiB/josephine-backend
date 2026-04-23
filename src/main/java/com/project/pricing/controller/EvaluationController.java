package com.project.pricing.controller;

import com.project.pricing.model.PriceRecord;
import com.project.pricing.model.Product;
import com.project.pricing.repository.PriceRecordRepository;
import com.project.pricing.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/evaluation")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EvaluationController {

    private final ProductRepository productRepository;
    private final PriceRecordRepository priceRecordRepository;

    @GetMapping("/dataset")
    public ResponseEntity<List<Map<String, Object>>> getEvaluationDataset() {
        List<Product> products = productRepository.findAll();
        List<Map<String, Object>> dataset = new ArrayList<>();

        for (Product p : products) {
            List<PriceRecord> records = priceRecordRepository.findByProductId(p.getId());
            if (records.isEmpty()) continue;

            double medianPrice = calculateMedian(records);
            
            Map<String, Object> entry = new HashMap<>();
            entry.put("productId", p.getId());
            entry.put("productName", p.getName());
            entry.put("category", p.getCategory());
            entry.put("recordCount", records.size());
            entry.put("marketTruthPrice", medianPrice);
            
            // Source breakdown
            Map<String, Double> sourceDeviations = new HashMap<>();
            records.stream()
                .collect(Collectors.groupingBy(PriceRecord::getSource))
                .forEach((source, sourceRecords) -> {
                    double sourceAvg = sourceRecords.stream().mapToDouble(PriceRecord::getPrice).average().orElse(0.0);
                    double dev = Math.abs(sourceAvg - medianPrice) / medianPrice * 100;
                    sourceDeviations.put(source, dev);
                });
            
            entry.put("sourceDeviationsPct", sourceDeviations);
            dataset.add(entry);
        }

        return ResponseEntity.ok(dataset);
    }

    @GetMapping("/performance")
    public ResponseEntity<Map<String, Object>> getModelPerformance() {
        // Mocked for now, in a real scenario this would aggregate from PredictionResult vs actual subsequent prices
        return ResponseEntity.ok(Map.of(
            "mape", 4.2, // Mean Absolute Percentage Error
            "rmse", 0.15, // Root Mean Square Error
            "anomalyPrecision", 0.92,
            "anomalyRecall", 0.88,
            "evaluationDate", new Date().toString()
        ));
    }

    private double calculateMedian(List<PriceRecord> records) {
        List<Double> prices = records.stream()
            .map(PriceRecord::getPrice)
            .sorted()
            .toList();
        
        int size = prices.size();
        if (size == 0) return 0.0;
        if (size % 2 == 0) {
            return (prices.get(size / 2 - 1) + prices.get(size / 2)) / 2.0;
        } else {
            return prices.get(size / 2);
        }
    }
}
