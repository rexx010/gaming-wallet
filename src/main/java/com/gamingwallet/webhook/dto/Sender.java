package com.gamingwallet.webhook.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class Sender {

    @JsonAlias("account_name")
    private String accountName;

    @JsonAlias("account_number")
    private String accountNumber;

    @JsonAlias("bank_name")
    private String bankName;

}