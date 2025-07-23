package com.productEvee.productEvee.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    @Value("${onesignal.app-id}")
    private String appId;

    @Value("${onesignal.api-key}")
    private String apiKey;

    public void sendNotification(List<String> playerIds, String title, String message) {
        if (playerIds.isEmpty()) return;

        RestTemplate restTemplate = new RestTemplate();

        String url = "https://api.onesignal.com/notifications";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + apiKey);

        Map<String, Object> body = Map.of(
                "app_id", appId,
                "include_player_ids", playerIds,
                "headings", Map.of("en", title),
                "contents", Map.of("en", message)
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            System.out.println("OneSignal response: " + response.getBody());
        } catch (Exception e) {
            System.err.println("Erro ao enviar notificação: " + e.getMessage());
        }
    }
}
