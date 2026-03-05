package com.project.pricing.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "risk_scores")
public class RiskScore {
    @Id
    private String id;
    private String productId;
    private double score; // 0-100
    private String classification; // Low, Moderate, High
    private LocalDateTime timestamp;
    private String details; // Reason/Logic calculation
}
