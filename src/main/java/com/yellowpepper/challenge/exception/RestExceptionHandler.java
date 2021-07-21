package com.yellowpepper.challenge.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yellowpepper.challenge.dto.ResponseBaseDto;
import com.yellowpepper.challenge.dto.ResponseCreateTransactionDto;
import com.yellowpepper.challenge.dto.ResponseRetrieveAccountDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestControllerAdvice
public class RestExceptionHandler {
  private static final String STATUS = "status";
  private static final String DESCRIPTION = "description";
  private static final String STATUS_KO = "KO";

  private static final Logger LOGGER = Logger.getLogger(RestExceptionHandler.class.getName());

  private final ObjectMapper mapper = new ObjectMapper();

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleInternalServer(HttpServletRequest req, Exception ex) {
    ObjectNode errors = mapper.createObjectNode();
    errors.put(STATUS, STATUS_KO);
    errors.putPOJO(DESCRIPTION, ex.getMessage());
    LOGGER.log(Level.SEVERE, "an exception was thrown", ex);
    return new ResponseEntity<>(errors, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(AccountNotFoundException.class)
  public ResponseEntity<Object> accountNotFound(
      HttpServletRequest req, AccountNotFoundException ex) {
    ResponseRetrieveAccountDto responseRetrieveAccountDto = new ResponseRetrieveAccountDto();
    responseRetrieveAccountDto.setStatus(STATUS_KO);
    responseRetrieveAccountDto.addError(ex.getMessage());
    return new ResponseEntity<>(
        responseRetrieveAccountDto, new HttpHeaders(), HttpStatus.NOT_FOUND);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({CurrencyNotSupportedException.class, WrongInformationException.class})
  public ResponseEntity<Object> currencyNotSupported(HttpServletRequest req, RuntimeException ex) {
    ResponseCreateTransactionDto responseCreateTransactionDto = new ResponseCreateTransactionDto();
    responseCreateTransactionDto.setStatus(STATUS_KO);
    responseCreateTransactionDto.addError(ex.getMessage());
    return new ResponseEntity<>(
        responseCreateTransactionDto, new HttpHeaders(), HttpStatus.BAD_REQUEST);
  }

  @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
  @ExceptionHandler({InsufficientFundsException.class, AccountTransfersLimitExceedException.class})
  public ResponseEntity<Object> insufficientFunds(HttpServletRequest req, RuntimeException ex) {
    ResponseCreateTransactionDto responseCreateTransactionDto = new ResponseCreateTransactionDto();
    responseCreateTransactionDto.setStatus(STATUS_KO);
    responseCreateTransactionDto.addError(ex.getMessage());
    return new ResponseEntity<>(
        responseCreateTransactionDto, new HttpHeaders(), HttpStatus.PRECONDITION_FAILED);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(CustomerNotFoundException.class)
  public ResponseEntity<Object> customerNotFound(
      HttpServletRequest req, CustomerNotFoundException ex) {
    ResponseBaseDto responseBaseDto = new ResponseBaseDto();
    responseBaseDto.setStatus(STATUS_KO);
    responseBaseDto.addError(ex.getMessage());
    return new ResponseEntity<>(responseBaseDto, new HttpHeaders(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException e) {
    ObjectNode errors = mapper.createObjectNode();
    errors.put(STATUS, STATUS_KO);
    errors.put(DESCRIPTION, e.getMessage());
    return ResponseEntity.status(e.getStatus()).body(errors);
  }
}
