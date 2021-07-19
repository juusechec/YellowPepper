package com.yellowpepper.challenge.exception;

public class AccountTransfersLimitExceedException extends RuntimeException {
  private static final long serialVersionUID = -1010721230493440024L;

  public AccountTransfersLimitExceedException(String message) {
    super(message);
  }
}
