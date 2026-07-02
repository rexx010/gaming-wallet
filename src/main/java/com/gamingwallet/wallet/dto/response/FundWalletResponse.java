package com.gamingwallet.wallet.dto.response;

public record FundWalletResponse(
        String reference,
        String status,
        String message,
        String accountNumber,
        String bankName,
        String accountName
) {
}
