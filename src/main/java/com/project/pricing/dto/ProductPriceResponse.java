package com.project.pricing.dto;

import com.project.pricing.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPriceResponse {
    private String id;
    private String name;
    private String brand;
    private String category;
    private String unitOfMeasure;
    private double currentPrice;
    private String retailer;
    private String region;
    private String lastUpdated;

    public static ProductPriceResponse fromProduct(Product product, double price, String retailer, String region,
            String timestamp) {
        return ProductPriceResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .brand(product.getBrand())
                .category(product.getCategory())
                .unitOfMeasure(product.getUnitOfMeasure())
                .currentPrice(price)
                .retailer(retailer)
                .region(region)
                .lastUpdated(timestamp)
                .build();
    }
}
