package com.yellowpepper.challenge.domain;

import java.math.BigDecimal;

public class TaxTransferRequest {
    private BigDecimal amount;
    private double tax;

    public TaxTransferRequest(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }
}
