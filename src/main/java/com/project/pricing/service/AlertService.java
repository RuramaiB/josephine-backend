package com.project.pricing.service;

import com.project.pricing.model.Alert;
import com.project.pricing.model.RiskScore;
import com.project.pricing.repository.AlertRepository;
import com.project.pricing.repository.RiskScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final RiskScoreRepository riskScoreRepository;

    public void checkAndTriggerAlerts(String productId) {
        riskScoreRepository.findFirstByProductIdOrderByTimestampDesc(productId)
                .ifPresent(risk -> {
                    if (risk.getScore() > 70) {
                        createAlert(productId, "HIGH_RISK_OVERPRICING",
                                "High risk of overpricing detected: " + risk.getClassification(), risk.getScore());
                    }
                });
    }

    private void createAlert(String productId, String type, String message, double score) {
        Alert alert = new Alert();
        alert.setProductId(productId);
        alert.setType(type);
        alert.setMessage(message);
        alert.setRiskScore(score);
        alert.setStatus("NEW");
        alert.setTimestamp(LocalDateTime.now());
        alertRepository.save(alert);
    }

    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }
}
