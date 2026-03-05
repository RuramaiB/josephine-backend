package com.project.pricing.service;

import com.project.pricing.model.PriceRecord;
import com.project.pricing.repository.PriceRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

        private final PriceRecordRepository priceRecordRepository;

        public Map<String, Object> getProductAnalytics(String productId) {
                List<PriceRecord> history = priceRecordRepository.findByProductId(productId);

                if (history.isEmpty())
                        return Map.of();

                DoubleSummaryStatistics stats = history.stream()
                                .mapToDouble(PriceRecord::getPrice)
                                .summaryStatistics();

                double movingAverage = history.stream()
                                .skip(Math.max(0, history.size() - 7)) // last 7 records
                                .mapToDouble(PriceRecord::getPrice)
                                .average().orElse(0.0);

                double stdDev = Math.sqrt(history.stream()
                                .mapToDouble(p -> Math.pow(p.getPrice() - stats.getAverage(), 2))
                                .average().orElse(0.0));

                return Map.of(
                                "productId", productId,
                                "min", stats.getMin(),
                                "max", stats.getMax(),
                                "average", stats.getAverage(),
                                "movingAverage7", movingAverage,
                                "standardDeviation", stdDev,
                                "volatilityIndex", stdDev / stats.getAverage());
        }
}
