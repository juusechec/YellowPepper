package com.yellowpepper.challenge.controller;

import com.yellowpepper.challenge.dto.RequestCreateTransactionDto;
import com.yellowpepper.challenge.dto.RequestRetrieveAccountDto;
import com.yellowpepper.challenge.dto.ResponseCreateTransactionDto;
import com.yellowpepper.challenge.dto.ResponseRetrieveAccountDto;
import com.yellowpepper.challenge.repository.model.Account;
import com.yellowpepper.challenge.repository.model.Customer;
import com.yellowpepper.challenge.service.AccountService;
import com.yellowpepper.challenge.service.CustomerService;
import com.yellowpepper.challenge.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class FixedDepositRateController {

  @Autowired AccountService retrieveAccountService;

  @Autowired TransferService transferService;

  @Autowired CustomerService customerService;

  @GetMapping(value = "/v1/customers/{customerId}", produces = "application/json")
  public Mono<Customer> getCustomer(@PathVariable Integer customerId) {
    return customerService.getCustomer(customerId);
  }

  @GetMapping(value = "/v1/customers/{customerId}/accounts", produces = "application/json")
  public Flux<Account> getCustomerAccounts(@PathVariable Integer customerId) {
    return customerService.getCustomerAccounts(customerId);
  }

  @GetMapping(
      value = "/v1/customers/{customerId}/accounts/{accountId}",
      produces = "application/json")
  public Mono<Account> getCustomerAccounts(
      @PathVariable Integer customerId, @PathVariable Integer accountId) {
    return customerService
        .getCustomerAccounts(customerId)
        .filter(account -> account.getId().equals(accountId))
        .next();
  }

  @PostMapping(value = "/v1/customers/{customerId}/retrieve-account", produces = "application/json")
  public Mono<ResponseRetrieveAccountDto> getAccount(
      @RequestBody RequestRetrieveAccountDto requestRetrieveAccountDto) {
    return retrieveAccountService.getResponseBody(requestRetrieveAccountDto);
  }

  @PostMapping(value = "/v1/transactions", produces = "application/json")
  public Mono<ResponseCreateTransactionDto> createTransaction(
      @RequestBody RequestCreateTransactionDto requestCreateTransactionDto) {
    return transferService.createTransfer(requestCreateTransactionDto);
  }
}
