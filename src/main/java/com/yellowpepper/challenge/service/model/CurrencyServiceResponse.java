package com.yellowpepper.challenge.service.model;

public class CurrencyServiceResponse {
  private Boolean success;

  private Long timestamp;

  private String base;

  private String date;

  private Rates rates;

  public Boolean getSuccess() {
    return success;
  }

  public void setSuccess(Boolean success) {
    this.success = success;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public String getBase() {
    return base;
  }

  public void setBase(String base) {
    this.base = base;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public Rates getRates() {
    return rates;
  }

  public void setRates(Rates rates) {
    this.rates = rates;
  }
}
