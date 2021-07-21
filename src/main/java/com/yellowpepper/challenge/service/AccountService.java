package com.yellowpepper.challenge.service;

import com.yellowpepper.challenge.dto.RequestRetrieveAccountDto;
import com.yellowpepper.challenge.dto.ResponseRetrieveAccountDto;
import com.yellowpepper.challenge.exception.AccountNotFoundException;
import com.yellowpepper.challenge.repository.AccountRepository;
import com.yellowpepper.challenge.repository.CurrencyRepository;
import com.yellowpepper.challenge.repository.model.Account;
import com.yellowpepper.challenge.repository.model.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
public class AccountService {
  @Autowired AccountRepository accountRepository;

  @Autowired CurrencyRepository currencyRepository;

  public Mono<ResponseRetrieveAccountDto> getResponseBody(
      RequestRetrieveAccountDto requestRetrieveAccountDto) {
    Integer accountId = Integer.valueOf(requestRetrieveAccountDto.getAccount());
    Mono<Account> accountMono = accountRepository.findById(accountId);
    return accountMono
        .flatMap(
            account -> {
              Mono<Currency> currency = currencyRepository.findById(account.getIdCurrency());
              return Mono.zip(Mono.just(account), currency);
            })
        .map(
            data -> {
              Account account = data.getT1();
              Currency currency = data.getT2();
              return getResponseObject("OK", account.getAmount(), currency.getAbbreviation());
            })
        .switchIfEmpty(
            Mono.defer(
                () -> {
                  throw new AccountNotFoundException(
                      String.format("Account with id %s not found in the database", accountId));
                }));
  }

  public ResponseRetrieveAccountDto getResponseObject(
      String status, BigDecimal amount, String currency) {
    ResponseRetrieveAccountDto responseRetrieveAccountDto = new ResponseRetrieveAccountDto();
    responseRetrieveAccountDto.setStatus(status);
    responseRetrieveAccountDto.setAccountBalance(amount);
    responseRetrieveAccountDto.setCurrency(currency);
    return responseRetrieveAccountDto;
  }
}
