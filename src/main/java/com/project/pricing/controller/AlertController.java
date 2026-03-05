package com.project.pricing.controller;

import com.project.pricing.model.Alert;
import com.project.pricing.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping
    public List<Alert> getAlerts() {
        return alertService.getAllAlerts();
    }
}
