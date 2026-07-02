package com.gamingwallet.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class KeepAliveScheduler {
    @Value("${APP_URL:}")
    private String appUrl;


    @Scheduled(fixedRate = 840000, initialDelay = 60000)
    public void keepAlive() {
        if (appUrl == null || appUrl.isBlank()) {
            // Don't ping anything in local dev — APP_URL isn't set locally
            return;
        }

        try {
            RestClient client = RestClient.create();
            String response = client
                    .get()
                    .uri(appUrl + "/health")
                    .retrieve()
                    .body(String.class);
            log.info("[keep-alive] pinged {} → {}", appUrl + "/health", response);
        } catch (Exception ex) {
            // Don't let a failed ping crash the scheduler thread or affect
            // any user-facing functionality — just log and move on.
            log.warn("[keep-alive] ping failed: {}", ex.getMessage());
        }
    }
}