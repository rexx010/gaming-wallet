package com.gamingwallet.spotflow.dto;

public record DynamicAccountRequest(
        String currency,
        String accountName,
        Integer amount,
        Integer expiresIn
) {
}
