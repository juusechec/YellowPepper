package com.yellowpepper.challenge.domain;

import com.yellowpepper.challenge.service.enums.AvailabilityTransfer;

import java.math.BigDecimal;

public class AccountTransferValidationRequest {
  private BigDecimal amountToTransfer;
  private BigDecimal accountAmount;
  private BigDecimal tax;
  private Integer timesAccountTransaction;
  private AvailabilityTransfer availability;

  public AccountTransferValidationRequest(
      BigDecimal amountToTransfer,
      BigDecimal accountAmount,
      BigDecimal tax,
      Integer timesAccountTransaction) {
    this.amountToTransfer = amountToTransfer;
    this.accountAmount = accountAmount;
    this.tax = tax;
    this.timesAccountTransaction = timesAccountTransaction;
  }

  public BigDecimal getAmountToTransfer() {
    return amountToTransfer;
  }

  public void setAmountToTransfer(BigDecimal amountToTransfer) {
    this.amountToTransfer = amountToTransfer;
  }

  public BigDecimal getAccountAmount() {
    return accountAmount;
  }

  public void setAccountAmount(BigDecimal accountAmount) {
    this.accountAmount = accountAmount;
  }

  public AvailabilityTransfer getAvailability() {
    return availability;
  }

  public void setAvailability(String availability) {
    this.availability = AvailabilityTransfer.valueOf(availability);
  }

  public BigDecimal getTax() {
    return tax;
  }

  public void setTax(BigDecimal tax) {
    this.tax = tax;
  }

  public int getTimesAccountTransaction() {
    return timesAccountTransaction;
  }

  public void setTimesAccountTransaction(Integer timesAccountTransaction) {
    this.timesAccountTransaction = timesAccountTransaction;
  }

  public boolean getValid() {
    return accountAmount.compareTo(
            amountToTransfer.multiply(
                (tax.divide(BigDecimal.valueOf(100.0)).add(BigDecimal.valueOf(1.0)))))
        < 0;
  }
}
