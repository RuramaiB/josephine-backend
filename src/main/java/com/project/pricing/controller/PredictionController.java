package com.project.pricing.controller;

import com.project.pricing.model.PredictionResult;
import com.project.pricing.service.PredictionService;
import com.project.pricing.repository.PredictionResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/predictions")
@RequiredArgsConstructor
public class PredictionController {

    private final PredictionService predictionService;
    private final PredictionResultRepository predictionResultRepository;

    @PostMapping("/run/{productId}")
    public PredictionResult runPrediction(@PathVariable String productId) {
        return predictionService.runPrediction(productId);
    }

    @GetMapping("/{productId}")
    public PredictionResult getLatestPrediction(@PathVariable String productId) {
        return predictionResultRepository.findFirstByProductIdOrderByTimestampDesc(productId)
                .orElseThrow(() -> new RuntimeException("No prediction found for product"));
    }
}
