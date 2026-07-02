package com.gamingwallet.spotflow.dto.payout;

public record DestinationRequest(
        String accountNumber,
        String accountName,
        String bankCode
) {
}
