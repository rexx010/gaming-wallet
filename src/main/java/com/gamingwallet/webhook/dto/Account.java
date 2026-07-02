package com.gamingwallet.webhook.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class Account {

    private String id;

    @JsonAlias("account_number")
    private String accountNumber;

    @JsonAlias("account_name")
    private String accountName;

}