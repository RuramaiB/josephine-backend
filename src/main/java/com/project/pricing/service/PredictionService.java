package com.project.pricing.service;

import com.project.pricing.dto.MLPriceDataDTO;
import com.project.pricing.integration.MLIntegrationService;
import com.project.pricing.model.PriceRecord;
import com.project.pricing.model.PredictionResult;
import com.project.pricing.repository.PriceRecordRepository;
import com.project.pricing.repository.PredictionResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PredictionService {

    private final MLIntegrationService mlIntegrationService;
    private final PriceRecordRepository priceRecordRepository;
    private final PredictionResultRepository predictionResultRepository;

    public PredictionResult runPrediction(String productId) {
        List<PriceRecord> history = priceRecordRepository.findByProductId(productId);

        List<MLPriceDataDTO> historyDTOs = history.stream()
                .map(p -> new MLPriceDataDTO(p.getProductId(), p.getPrice(), p.getTimestamp().toString()))
                .collect(Collectors.toList());

        var response = mlIntegrationService.getPrediction(productId, historyDTOs).block();

        PredictionResult result = new PredictionResult();
        result.setProductId(productId);
        result.setPredictedPrice(response.getPredictedPrice());
        result.setConfidence(response.getConfidence());
        result.setAnomalyProbability(response.getAnomalyProbability());
        result.setAnomaly(response.isAnomaly());
        result.setTimestamp(LocalDateTime.now());

        return predictionResultRepository.save(result);
    }
}
