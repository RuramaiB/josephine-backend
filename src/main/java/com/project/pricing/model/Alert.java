package com.project.pricing.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "alerts")
public class Alert {
    @Id
    private String id;
    private String productId;
    private String type;
    private String message;
    private double riskScore;
    private LocalDateTime timestamp;
    private String status; // NEW, READ, DISMISSED
}
