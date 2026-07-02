package com.gamingwallet.spotflow.dto.payout;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TransferResponse {

    private String reference;

    @JsonProperty("spotflowreference")
    private String spotflowReference;

    private Integer amount;

    private String currency;

    private String transferMode;

    private Destination destination;

    private String narrations;

    private String status;

    @Data
    public static class Destination {
        private String accountNumber;
        private String accountName;
        private String bankCode;
        private String bankName;
    }
}
