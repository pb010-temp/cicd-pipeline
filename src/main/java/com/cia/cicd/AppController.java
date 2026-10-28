package com.cia.cicd;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AppController {

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
            "application", "CIA 2 CI/CD Pipeline",
            "message", "Application deployed successfully!"
        );
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
}
