package com.project.pricing.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "prediction_results")
public class PredictionResult {
    @Id
    private String id;
    private String productId;
    private double predictedPrice;
    private double confidence;
    private double anomalyProbability;
    private boolean isAnomaly;
    private LocalDateTime timestamp;
}
