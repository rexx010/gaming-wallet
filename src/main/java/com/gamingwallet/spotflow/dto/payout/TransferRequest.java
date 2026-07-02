package com.gamingwallet.spotflow.dto.payout;

public record TransferRequest(
        String reference,

        Integer amount,

        String currency,

        DestinationRequest destination,

        String narration
) {
}
