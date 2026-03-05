package com.project.pricing.controller;

import com.project.pricing.model.PriceRecord;
import com.project.pricing.service.DataIngestionService;
import com.project.pricing.repository.PriceRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/prices")
@RequiredArgsConstructor
public class PriceController {

    private final DataIngestionService dataIngestionService;
    private final PriceRecordRepository priceRecordRepository;

    @PostMapping("/upload")
    public String uploadCsv(@RequestParam("file") MultipartFile file) throws Exception {
        dataIngestionService.uploadCsv(file);
        return "File uploaded and processing started";
    }

    @PostMapping("/manual")
    public void addManualEntry(@RequestParam String productId, @RequestParam double price,
            @RequestParam String source, @RequestParam String category, @RequestParam String unit) {
        // The following lines appear to be repository method signatures and are not
        // valid here.
        // Assuming the intent was to add these methods to the PriceRecordRepository
        // interface,
        // but they were mistakenly placed in the controller's method body.
        // To maintain syntactic correctness of the PriceController, these lines are
        // commented out.
        // List<PriceRecord> findByProductId(String productId);
        // List<PriceRecord> findBySource(String source);
        // List<PriceRecord> findByRegion(String region);
        // List<PriceRecord> findBySourceAndRegion(String source, String region);
        dataIngestionService.ingestManualEntry(productId, price, source, category, unit);
    }

    @GetMapping
    public List<PriceRecord> getAllPrices() {
        return priceRecordRepository.findAll();
    }
}
