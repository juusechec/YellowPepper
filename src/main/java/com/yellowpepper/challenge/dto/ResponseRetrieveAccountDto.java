package com.yellowpepper.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class ResponseRetrieveAccountDto extends ResponseBaseDto {
  @JsonProperty(value = "account_balance")
  private BigDecimal accountBalance;

  private String currency;

  public ResponseRetrieveAccountDto() {
    super();
    this.accountBalance = BigDecimal.valueOf(0);
  }

  public BigDecimal getAccountBalance() {
    return accountBalance;
  }

  public void setAccountBalance(BigDecimal accountBalance) {
    this.accountBalance = accountBalance;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }
}
