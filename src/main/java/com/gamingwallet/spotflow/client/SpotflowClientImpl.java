package com.gamingwallet.spotflow.client;

import com.gamingwallet.spotflow.dto.DynamicAccountRequest;
import com.gamingwallet.spotflow.dto.DynamicAccountResponse;
import com.gamingwallet.spotflow.dto.payout.TransferRequest;
import com.gamingwallet.spotflow.dto.payout.TransferResponse;
import com.gamingwallet.wallet.dto.request.DisbursementRequest;
import com.gamingwallet.wallet.dto.response.DisbursementResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class SpotflowClientImpl implements SpotflowClient{
    private final RestClient restClient;

    public SpotflowClientImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public DynamicAccountResponse createDynamicAccount(DynamicAccountRequest request) {
        try{
            return restClient.post()
                    .uri("/virtual-accounts/temporary")
                    .body(request)
                    .retrieve()
                    .body(DynamicAccountResponse.class);
            }catch (HttpClientErrorException ex) {
            throw new RuntimeException("Failed to create virtual account", ex);
        }
    }

    @Override
    public TransferResponse getTransfer(String reference) {
        return restClient.get()
                .uri("/transfers/reference/{reference}", reference)
                .retrieve()
                .body(TransferResponse.class);

    }

    @Override
    public TransferResponse createTransfer(TransferRequest request) {
        return restClient.post()
                .uri("/transfers")
                .body(request)
                .retrieve()
                .body(TransferResponse.class);
    }
}
