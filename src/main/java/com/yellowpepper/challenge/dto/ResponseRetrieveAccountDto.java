package com.yellowpepper.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class ResponseRetrieveAccountDto extends ResponseBaseDto {
    @JsonProperty(value= "account_balance")
    private BigDecimal accountBalance;

    public ResponseRetrieveAccountDto() {
        super();
    }

    public BigDecimal getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(BigDecimal accountBalance) {
        this.accountBalance = accountBalance;
    }
}
