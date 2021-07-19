package com.yellowpepper.challenge.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yellowpepper.challenge.dto.ResponseCreateTransactionDto;
import com.yellowpepper.challenge.dto.ResponseRetrieveAccountDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class RestExceptionHandler {
    private static final String STATUS = "status";
    private static final String DESCRIPTION = "description";
    private static final String STATUS_KO = "KO";

    private ObjectMapper mapper = new ObjectMapper();

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleInternalServer(HttpServletRequest req, Exception ex) {
        ObjectNode errors = mapper.createObjectNode();
        errors.put(STATUS, STATUS_KO);
        errors.putPOJO(DESCRIPTION, ex.getCause());
        ex.printStackTrace();
        return new ResponseEntity<>(errors, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Object> accountNotFound(HttpServletRequest req, AccountNotFoundException ex) {
        ResponseRetrieveAccountDto responseRetrieveAccountDto = new ResponseRetrieveAccountDto();
        responseRetrieveAccountDto.setStatus(STATUS_KO);
        responseRetrieveAccountDto.addError(ex.getMessage());
        return new ResponseEntity<>(responseRetrieveAccountDto, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CurrencyNotSupportedException.class)
    public ResponseEntity<Object> currencyNotSupported(HttpServletRequest req, CurrencyNotSupportedException ex) {
        ResponseCreateTransactionDto responseCreateTransactionDto = new ResponseCreateTransactionDto();
        responseCreateTransactionDto.setStatus(STATUS_KO);
        responseCreateTransactionDto.addError(ex.getMessage());
        return new ResponseEntity<>(responseCreateTransactionDto, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Object> insufficientFunds(HttpServletRequest req, InsufficientFundsException ex) {
        ResponseCreateTransactionDto responseCreateTransactionDto = new ResponseCreateTransactionDto();
        responseCreateTransactionDto.setStatus(STATUS_KO);
        responseCreateTransactionDto.addError(ex.getMessage());
        return new ResponseEntity<>(responseCreateTransactionDto, new HttpHeaders(), HttpStatus.PRECONDITION_FAILED);
    }
}
