package com.yellowpepper.challenge.exception;

public class WrongInformationException extends RuntimeException {
  private static final long serialVersionUID = -6710921338428440023L;

  public WrongInformationException(String message) {
    super(message);
  }
}
