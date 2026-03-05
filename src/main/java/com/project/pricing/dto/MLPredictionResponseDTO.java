package com.project.pricing.dto;

import lombok.Data;
import java.util.List;

@Data
public class MLPredictionResponseDTO {
    private String productId;
    private double predictedPrice;
    private double confidence;
    private boolean isAnomaly;
    private double anomalyProbability;
}
