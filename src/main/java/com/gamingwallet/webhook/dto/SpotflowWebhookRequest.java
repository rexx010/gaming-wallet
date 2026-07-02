package com.gamingwallet.webhook.dto;

import lombok.Data;

@Data
public class SpotflowWebhookRequest {
    private String event;
    private WebhookData data;
}