package com.gamingwallet.wallet.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record FundWalletRequest(
        @NotNull
        Long userId,

        @NotNull
        @DecimalMin("1.00")
        BigDecimal amount
) {
}
