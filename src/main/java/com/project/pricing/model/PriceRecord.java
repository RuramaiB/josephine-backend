package com.project.pricing.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "price_records")
public class PriceRecord {
    @Id
    private String id;

    @Indexed
    private String productId;

    private double price;
    private LocalDateTime timestamp;
    private String source; // e.g., OK Zimbabwe, TM Pick n Pay, Puma Fuel
    private String category; // e.g., Grocery, Fuel, Grain
    private String unit; // e.g., 1L, 1kg
    private String region; // e.g., Harare, Bulawayo, Mutare
    private String link; // Direct source URL
    private double reliability; // 0.0 to 1.0
    private boolean isAlert;
    private double riskScore;
}
