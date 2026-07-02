package com.gamingwallet.spotflow.client;

import com.gamingwallet.spotflow.dto.DynamicAccountRequest;
import com.gamingwallet.spotflow.dto.DynamicAccountResponse;
import com.gamingwallet.spotflow.dto.payout.TransferRequest;
import com.gamingwallet.spotflow.dto.payout.TransferResponse;

public interface SpotflowClient {
    DynamicAccountResponse createDynamicAccount(DynamicAccountRequest request);
    TransferResponse createTransfer(TransferRequest request);
    TransferResponse getTransfer(String reference);
}
