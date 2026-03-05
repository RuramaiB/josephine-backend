package com.project.pricing.service;

import com.project.pricing.model.PriceRecord;
import com.project.pricing.model.PredictionResult;
import com.project.pricing.model.RiskScore;
import com.project.pricing.repository.RiskScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RiskScoringService {

    private final RiskScoreRepository riskScoreRepository;

    public RiskScore calculateRiskScore(String productId, double actualPrice, PredictionResult prediction,
            List<PriceRecord> history) {
        double predictedPrice = prediction.getPredictedPrice();

        // 1. Percentage Deviation
        double percentDev = Math.abs((actualPrice - predictedPrice) / predictedPrice) * 100;

        // 2. Z-Score (simplified)
        double mean = history.stream().mapToDouble(PriceRecord::getPrice).average().orElse(actualPrice);
        double stdDev = Math.sqrt(history.stream()
                .mapToDouble(p -> Math.pow(p.getPrice() - mean, 2))
                .average().orElse(1.0));
        double zScore = Math.abs((actualPrice - mean) / (stdDev == 0 ? 1 : stdDev));

        // 3. ML Anomaly Probability
        double anomalyProb = prediction.getAnomalyProbability() * 100;

        // Final weighted score calculation
        double finalScore = (percentDev * 0.4) + (zScore * 10 * 0.3) + (anomalyProb * 0.3);
        finalScore = Math.min(100, Math.max(0, finalScore));

        RiskScore risk = new RiskScore();
        risk.setProductId(productId);
        risk.setScore(finalScore);
        risk.setTimestamp(LocalDateTime.now());

        if (finalScore <= 30)
            risk.setClassification("Low Risk");
        else if (finalScore <= 70)
            risk.setClassification("Moderate");
        else
            risk.setClassification("High Risk");

        risk.setDetails(String.format("Dev: %.2f%%, Z: %.2f, Anomaly: %.2f%%", percentDev, zScore, anomalyProb));

        return riskScoreRepository.save(risk);
    }
}
