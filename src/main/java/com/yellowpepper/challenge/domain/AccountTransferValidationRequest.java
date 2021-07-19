package com.yellowpepper.challenge.domain;

import com.yellowpepper.challenge.service.enums.AvailabilityTransfer;

import java.math.BigDecimal;

public class AccountTransferValidationRequest {
    private BigDecimal amountToTransfer;
    private BigDecimal accountAmount;
    private Double tax;
    private Integer timesAccountTransaction;
    private AvailabilityTransfer availability;

    public AccountTransferValidationRequest(BigDecimal amountToTransfer, BigDecimal accountAmount, Double tax, Integer timesAccountTransaction) {
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

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    public int getTimesAccountTransaction() {
        return timesAccountTransaction;
    }

    public void setTimesAccountTransaction(Integer timesAccountTransaction) {
        this.timesAccountTransaction = timesAccountTransaction;
    }
}
