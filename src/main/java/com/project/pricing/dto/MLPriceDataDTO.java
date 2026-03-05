package com.project.pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MLPriceDataDTO {
    private String productId;
    private double price;
    private String timestamp;
}
