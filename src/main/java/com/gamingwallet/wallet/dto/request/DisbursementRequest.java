package com.gamingwallet.wallet.dto.request;

import java.math.BigDecimal;

public record DisbursementRequest(
        BigDecimal amount,

        String bankCode,

        String accountNumber,

        String narration,

        String currency,

        String reference
) {
}
