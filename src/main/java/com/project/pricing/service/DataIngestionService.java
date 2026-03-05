package com.project.pricing.service;

import com.project.pricing.model.PriceRecord;
import com.project.pricing.repository.PriceRecordRepository;
import com.project.pricing.repository.ProductRepository;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataIngestionService {

    private final PriceRecordRepository priceRecordRepository;
    private final ScrapingService scrapingService;
    private final AnalysisService analysisService;

    @org.springframework.scheduling.annotation.Async
    public void triggerAllScrapers() {
        scrapingService.scrapeAll();
        analysisService.analyzeRecentPrices();
    }

    public void ingestManualEntry(String productId, double price, String source, String category, String unit) {
        PriceRecord record = PriceRecord.builder()
                .productId(productId)
                .price(price)
                .source(source)
                .category(category)
                .unit(unit)
                .timestamp(LocalDateTime.now())
                .reliability(1.0)
                .build();
        priceRecordRepository.save(record);
        analysisService.detectOverpricing(record);
    }

    public void uploadCsv(MultipartFile file) throws Exception {
        // ... implementation for CSV ingestion ...
    }
}
