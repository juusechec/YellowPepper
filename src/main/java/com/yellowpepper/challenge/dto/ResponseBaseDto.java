package com.yellowpepper.challenge.dto;

public class ResponseBaseDto {
    private String status;

    private String[] errors;

    public ResponseBaseDto() {
        this.errors = new String[]{};
    }

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

    public void addError(String error) {
        this.errors = new String []{error};
    }
}
