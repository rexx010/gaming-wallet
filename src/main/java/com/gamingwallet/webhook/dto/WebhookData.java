package com.gamingwallet.webhook.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class WebhookData {

    private String id;

    private String reference;

    @JsonAlias({"spotflow_reference", "spotflowReference"})
    private String spotflowReference;

    private BigDecimal amount;

    private BigDecimal fee;

    private String currency;

    private String status;

    private String type;

    private String source;

    private String description;

    private String mode;

    private Account account;

    private Sender sender;

    private Destination destination;

    @JsonAlias({"transaction_date", "createdAt"})
    private OffsetDateTime transactionDate;

}
