package com.gamingwallet.spotflow.service;

import com.gamingwallet.spotflow.dto.DynamicAccountResponse;
import com.gamingwallet.spotflow.dto.payout.TransferResponse;
import com.gamingwallet.wallet.dto.request.WithdrawRequest;

import java.math.BigDecimal;

public interface SpotflowService {
    DynamicAccountResponse createTemporaryAccount(
            String accountName,
            BigDecimal amount
    );

    TransferResponse transfer(
            WithdrawRequest request,
            String reference
    );

    TransferResponse getTransfer(String reference);
}
