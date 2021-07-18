package com.yellowpepper.challenge.dto;

import java.math.BigDecimal;

public class ResponseCreateTransactionDto {
    private String status;
    private String[] errors;
    private BigDecimal account_balance;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String[] getErrors() {
        return errors;
    }

    public void setErrors(String[] errors) {
        this.errors = errors;
    }

    public BigDecimal getAccount_balance() {
        return account_balance;
    }

    public void setAccount_balance(BigDecimal account_balance) {
        this.account_balance = account_balance;
    }
}
