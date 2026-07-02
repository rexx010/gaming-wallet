package com.gamingwallet.wallet.dto.response;

public record WithdrawResponse(
        String reference,

        String status,

        String message
) {
}
