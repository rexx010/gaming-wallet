package com.gamingwallet.webhook.service;

import com.gamingwallet.webhook.dto.SpotflowWebhookRequest;

public interface WebhookService {
    void processWebhook(SpotflowWebhookRequest request);
}
