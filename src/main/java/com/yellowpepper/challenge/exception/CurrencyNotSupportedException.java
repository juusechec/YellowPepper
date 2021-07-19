package com.yellowpepper.challenge.exception;

public class CurrencyNotSupportedException extends RuntimeException {
  private static final long serialVersionUID = -6810921330428440023L;

  public CurrencyNotSupportedException(String message) {
    super(message);
  }
}
