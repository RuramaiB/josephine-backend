package com.project.pricing.controller;

import com.project.pricing.model.RiskScore;
import com.project.pricing.repository.RiskScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/risk")
@RequiredArgsConstructor
public class RiskController {

    private final RiskScoreRepository riskScoreRepository;

    @GetMapping("/{productId}")
    public RiskScore getRiskScore(@PathVariable String productId) {
        return riskScoreRepository.findFirstByProductIdOrderByTimestampDesc(productId)
                .orElseThrow(() -> new RuntimeException("Risk score not found"));
    }
}
