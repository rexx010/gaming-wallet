package com.gamingwallet.wallet.dto.response;

import lombok.Data;

@Data
public class DisbursementResponse{
    private String reference;
    private String status;
    private String message;
}
