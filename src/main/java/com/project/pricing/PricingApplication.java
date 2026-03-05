package com.project.pricing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@org.springframework.scheduling.annotation.EnableAsync
public class PricingApplication {
    public static void main(String[] args) {
        SpringApplication.run(PricingApplication.class, args);
    }

    @org.springframework.context.annotation.Bean
    public org.springframework.boot.CommandLineRunner initData(
            com.project.pricing.service.DataIngestionService dataIngestionService) {
        return args -> {
            System.out.println("Triggering initial data scrapers on startup...");
            dataIngestionService.triggerAllScrapers();
        };
    }
}
