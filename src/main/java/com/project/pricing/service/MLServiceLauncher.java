package com.project.pricing.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
@Slf4j
public class MLServiceLauncher {

    @Value("${ml.service.path:./ml_service}")
    private String mlServicePath;

    @Value("${ml.service.python:python}")
    private String pythonCommand;

    private Process mlProcess;

    @PostConstruct
    public void startMLService() {
        log.info("Attempting to start Gwatidzo AI Service autonomously...");

        try {
            // Resolve path to the ML service directory
            File serviceDir = new File(mlServicePath);
            if (!serviceDir.isAbsolute()) {
                // Try several common locations
                File[] potentialPaths = {
                        new File(System.getProperty("user.dir"), mlServicePath),
                        new File(mlServicePath),
                        new File("ml_service")
                };
                for (File path : potentialPaths) {
                    if (path.exists() && path.isDirectory()) {
                        serviceDir = path;
                        break;
                    }
                }
            }

            if (!serviceDir.exists() || !serviceDir.isDirectory()) {
                log.error("Gwatidzo AI Service directory not found. Checked: {}, Root: {}",
                        serviceDir.getAbsolutePath(), System.getProperty("user.dir"));
                return;
            }

            log.info("Starting Gwatidzo AI Service in: {}", serviceDir.getAbsolutePath());

            // Check if uvicorn is available via python module
            String[] commands = { pythonCommand, "python3", "py", "python.exe" };
            boolean started = false;

            for (String cmd : commands) {
                try {
                    log.info("Trying to start AI Service with command: {} -m uvicorn main:app", cmd);
                    ProcessBuilder pb = new ProcessBuilder(cmd, "-m", "uvicorn", "main:app", "--host", "0.0.0.0",
                            "--port", "8000");
                    pb.directory(serviceDir);
                    pb.redirectErrorStream(true);
                    mlProcess = pb.start();

                    if (mlProcess.isAlive()) {
                        started = true;
                        log.info("Gwatidzo AI Service started successfully with command: {}", cmd);
                        break;
                    }
                } catch (IOException e) {
                    log.warn("Command '{}' failed to start AI Service: {}", cmd, e.getMessage());
                }
            }

            if (started) {
                new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(mlProcess.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            log.info("[AI-SERVICE] {}", line);
                        }
                    } catch (IOException e) {
                        log.error("AI Service logging interrupted", e);
                    }
                }).start();
            } else {
                log.error("Failed to start Gwatidzo AI Service using any known python command.");
            }

        } catch (Exception e) {
            log.error("Critical failure during Gwatidzo AI Service startup", e);
        }
    }

    @PreDestroy
    public void stopMLService() {
        if (mlProcess != null && mlProcess.isAlive()) {
            log.info("Shutting down ML Service...");
            mlProcess.destroy();
            try {
                if (!mlProcess.waitFor(5, java.util.concurrent.TimeUnit.SECONDS)) {
                    mlProcess.destroyForcibly();
                }
                log.info("ML Service shutdown complete.");
            } catch (InterruptedException e) {
                log.error("Error waiting for ML service shutdown", e);
                Thread.currentThread().interrupt();
            }
        }
    }
}
