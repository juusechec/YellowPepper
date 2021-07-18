package com.yellowpepper.challenge.exception;

public class AccountNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -6710921338428440023L;

    public AccountNotFoundException(String message) {
        super(message);
    }
}
