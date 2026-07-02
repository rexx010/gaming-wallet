package com.gamingwallet.wallet.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record WithdrawRequest(
        @NotNull
        Long userId,

        @DecimalMin("100")
        BigDecimal amount,

        @NotBlank
        String accountNumber,

        @NotBlank
        String accountName,

        @NotBlank
        String bankCode,

        String narration
) {
}
