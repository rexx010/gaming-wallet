package com.gamingwallet.webhook.controller;

import com.gamingwallet.common.dto.ApiResponse;
import com.gamingwallet.webhook.dto.SpotflowWebhookRequest;
import com.gamingwallet.webhook.service.WebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookService webhookService;

    @PostMapping("/spotflow")
    public ApiResponse<String> receiveWebhook(
            @RequestBody SpotflowWebhookRequest request
    ){
        webhookService.processWebhook(request);
        return new ApiResponse<>(
                true,
                "Webhook processed successfully",
                null
        );
    }
}
