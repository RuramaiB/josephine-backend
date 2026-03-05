package com.project.pricing.service;

import com.project.pricing.model.PriceRecord;
import com.project.pricing.repository.PriceRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisService {

    private final PriceRecordRepository priceRecordRepository;

    public void analyzeRecentPrices() {
        List<PriceRecord> recentRecords = priceRecordRepository.findAll().stream()
                .filter(r -> r.getTimestamp().isAfter(LocalDateTime.now().minusDays(7)))
                .collect(Collectors.toList());

        for (PriceRecord record : recentRecords) {
            detectOverpricing(record);
        }
    }

    public void detectOverpricing(PriceRecord record) {
        String productId = record.getProductId();
        List<PriceRecord> history = priceRecordRepository.findByProductId(productId);

        if (history.size() < 3)
            return; // Need at least 3 records for Basic Stats

        double sum = 0;
        for (PriceRecord r : history)
            sum += r.getPrice();
        double average = sum / history.size();

        double squareSum = 0;
        for (PriceRecord r : history) {
            squareSum += Math.pow(r.getPrice() - average, 2);
        }
        double stdDev = Math.sqrt(squareSum / history.size());

        // Simple Rule: Overpricing if price > average + 1.5 * stdDev
        boolean isOverpriced = record.getPrice() > (average + 1.5 * stdDev);
        record.setAlert(isOverpriced);

        // Calculate Risk Score (0-100)
        double riskScore = calculateRiskScore(record.getPrice(), average, stdDev);
        record.setRiskScore(riskScore);

        priceRecordRepository.save(record);
        if (isOverpriced) {
            log.warn("ALERT: Overpricing detected for {} at {} (Source: {}). Market average: {}",
                    productId, record.getPrice(), record.getSource(), average);
        }
    }

    private double calculateRiskScore(double price, double average, double stdDev) {
        if (stdDev == 0)
            return price > average ? 100 : 0;
        double zScore = (price - average) / stdDev;
        // Scale zScore to 0-100. 0 is no risk, 100 is extreme risk.
        double score = (zScore / 3.0) * 100; // Assuming 3 std devs is extreme
        return Math.min(Math.max(score, 0), 100);
    }
}
