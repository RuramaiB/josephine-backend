package com.project.pricing.integration;

import com.project.pricing.dto.MLPredictionResponseDTO;
import com.project.pricing.dto.MLPriceDataDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MLIntegrationService {

    private final WebClient mlWebClient;

    public Mono<MLPredictionResponseDTO> getPrediction(String productId, List<MLPriceDataDTO> history) {
        return mlWebClient.post()
                .uri("/ml/predict")
                .bodyValue(Map.of("productId", productId, "history", history))
                .retrieve()
                .bodyToMono(MLPredictionResponseDTO.class);
    }

    public Mono<Map> trainModel(List<MLPriceDataDTO> data) {
        return mlWebClient.post()
                .uri("/ml/train")
                .bodyValue(Map.of("data", data))
                .retrieve()
                .bodyToMono(Map.class);
    }
}
