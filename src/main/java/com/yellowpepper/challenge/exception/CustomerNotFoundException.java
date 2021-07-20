package com.yellowpepper.challenge.exception;

public class CustomerNotFoundException extends RuntimeException {
  private static final long serialVersionUID = -6710921338428440023L;

  public CustomerNotFoundException(String message) {
    super(message);
  }
}
