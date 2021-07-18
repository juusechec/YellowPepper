package com.yellowpepper.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class RequestCreateTransactionDto {
    private BigDecimal amount;

    private String currency;

    @JsonProperty(value= "origin_account")
    private Integer originAccount;

    @JsonProperty(value= "destination_account")
    private Integer destinationAccount;

    private String description;

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public Integer getOriginAccount() {
        return originAccount;
    }

    public Integer getDestinationAccount() {
        return destinationAccount;
    }

    public String getDescription() {
        return description;
    }
}
