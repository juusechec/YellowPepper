package com.yellowpepper.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class ResponseCreateTransactionDto extends ResponseBaseDto {
    @JsonProperty(value= "tax_collected")
    private BigDecimal taxCollected;

    @JsonProperty(value= "CAD")
    private BigDecimal canadianExchange;

    public ResponseCreateTransactionDto() {
        super();
        this.taxCollected = new BigDecimal(0);
    }

    public BigDecimal getTaxCollected() {
        return taxCollected;
    }

    public void setTaxCollected(BigDecimal taxCollected) {
        this.taxCollected = taxCollected;
    }

    public BigDecimal getCanadianExchange() {
        return canadianExchange;
    }

    public void setCanadianExchange(BigDecimal canadianExchange) {
        this.canadianExchange = canadianExchange;
    }
}
