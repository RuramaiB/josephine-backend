package com.project.pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketStatsDTO {
    private long totalProducts;
    private long totalPriceRecords;
    private double averageRefPrice;
    private double priceDisparity;
    private double indexAccuracy;
    private String latestUpdate;
}
