package com.gamingwallet.wallet.dto.response;

import java.math.BigDecimal;

public record VerifyWithdrawalResponse(
        String reference,

        String status,

        BigDecimal amount,

        String accountNumber,

        String accountName,

        String bankName
) {
}
