package com.gamingwallet.spotflow.dto;

public record DynamicAccountResponse(
        String id,
        String accountNumber,
        String accountName,
        String bankName,
        String mode,
        String lifeCycle
) {
}
