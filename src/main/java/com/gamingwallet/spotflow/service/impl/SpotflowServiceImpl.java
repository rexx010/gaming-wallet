package com.gamingwallet.spotflow.service.impl;

import com.gamingwallet.spotflow.client.SpotflowClient;
import com.gamingwallet.spotflow.dto.DynamicAccountRequest;
import com.gamingwallet.spotflow.dto.DynamicAccountResponse;
import com.gamingwallet.spotflow.dto.payout.DestinationRequest;
import com.gamingwallet.spotflow.dto.payout.TransferRequest;
import com.gamingwallet.spotflow.dto.payout.TransferResponse;
import com.gamingwallet.spotflow.service.SpotflowService;
import com.gamingwallet.wallet.dto.request.WithdrawRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SpotflowServiceImpl implements SpotflowService {
    private final SpotflowClient client;

    public final class SpotflowConstants {
        private SpotflowConstants() {}
        public static final String CURRENCY = "NGN";
        public static final int DEFAULT_EXPIRY_MINUTES = 30;
    }

    @Override
    public DynamicAccountResponse createTemporaryAccount(String accountName, BigDecimal amount) {

        int amountInKobo = amount.multiply(BigDecimal.valueOf(100)).intValue();
        DynamicAccountRequest request = new DynamicAccountRequest(
                SpotflowConstants.CURRENCY,
                accountName,
                amountInKobo,
                SpotflowConstants.DEFAULT_EXPIRY_MINUTES);
        return client.createDynamicAccount(request);
    }

    @Override
    public TransferResponse transfer(WithdrawRequest request, String reference) {
        int amountInKobo =
                request.amount()
                        .multiply(BigDecimal.valueOf(100))
                        .intValue();
        TransferRequest transferRequest = new TransferRequest(
                reference,
                amountInKobo,
                "NGN",
                new DestinationRequest(
                        request.accountNumber(),
                        request.accountName(),
                        request.bankCode()
                ),
                request.narration()
                );
        return client.createTransfer(transferRequest);
    }

    @Override
    public TransferResponse getTransfer(String reference) {
        return client.getTransfer(reference);
    }
}
