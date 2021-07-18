package com.yellowpepper.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class RequestCreateTransactionDto {
    private BigDecimal amount;

    private String currency;

    @JsonProperty(value= "origin_account")
    private String originAccount;

    @JsonProperty(value= "destination_account")
    private String destinationAccount;

    private String description;

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getOriginAccount() {
        return originAccount;
    }

    public String getDestinationAccount() {
        return destinationAccount;
    }

    public String getDescription() {
        return description;
    }
}
