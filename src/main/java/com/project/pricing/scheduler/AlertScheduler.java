package com.project.pricing.scheduler;

import com.project.pricing.model.Product;
import com.project.pricing.repository.ProductRepository;
import com.project.pricing.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlertScheduler {

    private final AlertService alertService;
    private final ProductRepository productRepository;

    @Scheduled(fixedRate = 3600000) // Every hour
    public void runAlertChecks() {
        log.info("Starting scheduled alert checks...");
        productRepository.findAll().forEach(product -> {
            alertService.checkAndTriggerAlerts(product.getId());
        });
        log.info("Completed alert checks.");
    }
}
