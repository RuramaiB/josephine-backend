package com.project.pricing.service;

import com.project.pricing.dto.MLPredictionResponseDTO;
import com.project.pricing.dto.MLPriceDataDTO;
import com.project.pricing.model.PriceRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GwatidzoIntelligenceService {

    private final ChatModel chatModel;

    /**
     * Analyses price history and generates predictions using Spring AI.
     * If AI service is unavailable, it uses a deterministic statistical fallback.
     */
    public MLPredictionResponseDTO analyzeMarketTrends(String productId, List<PriceRecord> history) {
        if (history == null || history.isEmpty()) {
            return mockResponse(productId);
        }

        try {
            String historyData = history.stream()
                    .map(p -> String.format("Date: %s, Price: %.4f", p.getTimestamp(), p.getPrice()))
                    .collect(Collectors.joining("\n"));

            String systemPrompt = """
                You are a Market Intelligence Analyst for Gwatidzo Price Index (Zimbabwe).
                Analyze the provided historical price data and predict the next price point.
                Detect if the latest price is an anomaly compared to the trend.
                Return ONLY a JSON object with fields: 
                "predictedPrice" (number), "confidence" (number 0-1), "isAnomaly" (boolean), "anomalyProbability" (number 0-1).
                Do not include any prose or explanation.
                """;

            String userPrompt = "Product ID: " + productId + "\nHistorical Data:\n" + historyData;

            String response = chatModel.call(new Prompt(List.of(
                    new SystemMessage(systemPrompt),
                    new UserMessage(userPrompt)
            ))).getResult().getOutput().getContent();

            log.info("AI Analysis Response for {}: {}", productId, response);
            
            return parseAiResponse(productId, response);

        } catch (Exception e) {
            log.warn("AI Market Intelligence unavailable (Key may be missing). Falling back to statistical engine for {}: {}", 
                    productId, e.getMessage());
            return runStatisticalAnalysis(productId, history);
        }
    }

    private MLPredictionResponseDTO runStatisticalAnalysis(String productId, List<PriceRecord> history) {
        double avg = history.stream().mapToDouble(PriceRecord::getPrice).average().orElse(0.0);
        double last = history.get(history.size() - 1).getPrice();
        
        double trend = (last - history.get(0).getPrice()) / Math.max(1, history.size() - 1);
        double prediction = last + trend;
        
        boolean isAnomaly = Math.abs(last - avg) / avg > 0.20;

        MLPredictionResponseDTO dto = new MLPredictionResponseDTO();
        dto.setProductId(productId);
        dto.setPredictedPrice(prediction);
        dto.setConfidence(0.85);
        dto.setAnomaly(isAnomaly);
        dto.setAnomalyProbability(isAnomaly ? 0.9 : 0.05);
        return dto;
    }

    private MLPredictionResponseDTO parseAiResponse(String productId, String json) {
        try {
            double predicted = Double.parseDouble(extractValue(json, "predictedPrice"));
            double confidence = Double.parseDouble(extractValue(json, "confidence"));
            boolean anomaly = json.contains("\"isAnomaly\": true");
            
            MLPredictionResponseDTO dto = new MLPredictionResponseDTO();
            dto.setProductId(productId);
            dto.setPredictedPrice(predicted);
            dto.setConfidence(confidence);
            dto.setAnomaly(anomaly);
            dto.setAnomalyProbability(anomaly ? 0.8 : 0.1);
            return dto;
        } catch (Exception e) {
            return mockResponse(productId);
        }
    }

    private String extractValue(String json, String key) {
        int start = json.indexOf("\"" + key + "\"") + key.length() + 3;
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        return json.substring(start, end).replace(":", "").replace("\"", "").trim();
    }

    private MLPredictionResponseDTO mockResponse(String productId) {
        MLPredictionResponseDTO dto = new MLPredictionResponseDTO();
        dto.setProductId(productId);
        dto.setPredictedPrice(0.0);
        dto.setConfidence(0.0);
        dto.setAnomaly(false);
        return dto;
    }

    /**
     * Generates a high-level market summary analysis using Ollama.
     */
    public String generateGlobalMarketSummary(String trendingSummary) {
        try {
            String systemPrompt = """
                You are a senior Market Analyst in Zimbabwe. 
                Analyze the current market landscape based on the summary provided.
                Provide a 2-3 sentence sophisticated brief for the national dashboard.
                Focus on price stability, inflation risks, and commodity availability.
                Be concise and professional.
                """;
            
            String userPrompt = "Current Market Snapshot:\n" + trendingSummary;

            return chatModel.call(new Prompt(List.of(
                    new SystemMessage(systemPrompt),
                    new UserMessage(userPrompt)
            ))).getResult().getOutput().getContent();
        } catch (Exception e) {
            log.error("AI Global Summary failed: {}", e.getMessage());
            return "Market remains stable with minor fluctuations in retail commodities.";
        }
    }

    /**
     * Handles natural language queries about the market using Ollama.
     * Injects current market data as context.
     */
    public String queryMarketAI(String query, String marketContext) {
        try {
            String systemPrompt = String.format("""
                You are the Gwatidzo Market Intelligence AI for Zimbabwe.
                Your goal is to provide accurate price predictions and market analysis based on real data.
                
                CURRENT MARKET CONTEXT:
                %s
                
                INSTRUCTIONS:
                1. Use the provided context to answer questions about prices, trends, and predictions.
                2. If the user asks for a prediction (e.g., 'sugar in 3 months'), analyze historical trends in the context.
                3. Be professional, concise, and highlight if certain prices are highly volatile.
                4. Always specify that these are AI-driven estimates based on current hub data.
                """, marketContext);

            return chatModel.call(new Prompt(List.of(
                    new SystemMessage(systemPrompt),
                    new UserMessage(query)
            ))).getResult().getOutput().getContent();
        } catch (Exception e) {
            log.error("AI Query failed: {}", e.getMessage());
            return "I'm currently recalibrating my market nodes. Please try again shortly.";
        }
    }
}
