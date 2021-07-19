package com.yellowpepper.challenge.domain;

import java.math.BigDecimal;

public class TaxTransferRequest {
    private BigDecimal amount;
    private Double tax;

    public TaxTransferRequest(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }
}
