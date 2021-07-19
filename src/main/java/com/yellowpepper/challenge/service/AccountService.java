package com.yellowpepper.challenge.service;

import com.yellowpepper.challenge.dto.RequestRetrieveAccountDto;
import com.yellowpepper.challenge.dto.ResponseRetrieveAccountDto;
import com.yellowpepper.challenge.exception.AccountNotFoundException;
import com.yellowpepper.challenge.repository.AccountRepository;
import com.yellowpepper.challenge.repository.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
public class AccountService {
    @Autowired
    AccountRepository accountRepository;

    public Mono<ResponseRetrieveAccountDto> getResponseBody(RequestRetrieveAccountDto requestRetrieveAccountDto) {
        Integer id = Integer.valueOf(requestRetrieveAccountDto.getAccount());
        Mono<Account> accountMono = accountRepository.findById(id);
        return accountMono.map(account -> getResponseObject("OK", account.getAmount()))
        .switchIfEmpty(Mono.defer(() -> {
            throw new AccountNotFoundException(String.format("User with id %s not found in the database", id));
        }));
    }

    public ResponseRetrieveAccountDto getResponseObject(String status, BigDecimal amount) {
        ResponseRetrieveAccountDto responseRetrieveAccountDto = new ResponseRetrieveAccountDto();
        responseRetrieveAccountDto.setStatus(status);
        responseRetrieveAccountDto.setAccountBalance(amount);
        return responseRetrieveAccountDto;
    }
}
