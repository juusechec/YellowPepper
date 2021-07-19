package com.yellowpepper.challenge.exception;

public class InsufficientFundsException extends RuntimeException {
  private static final long serialVersionUID = -1810921330420440024L;

  public InsufficientFundsException(String message) {
    super(message);
  }
}
