package com.yellowpepper.challenge.service;

import com.yellowpepper.challenge.dto.RequestRetrieveAccountDto;
import com.yellowpepper.challenge.dto.ResponseRetrieveAccountDto;
import com.yellowpepper.challenge.exception.AccountNotFoundException;
import com.yellowpepper.challenge.repository.AccountRepository;
import com.yellowpepper.challenge.repository.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AccountService {
    @Autowired
    AccountRepository accountRepository;

    public ResponseRetrieveAccountDto getResponseBody(RequestRetrieveAccountDto requestRetrieveAccountDto) {
        Integer id = Integer.valueOf(requestRetrieveAccountDto.getAccount());
        Mono<Account> accountMono = accountRepository.findById(id);
        Account account = accountMono.block();
        if (account == null) {
            throw new AccountNotFoundException(String.format("User with id %s not found in the database", id));
        } else {
            ResponseRetrieveAccountDto responseRetrieveAccountDto = new ResponseRetrieveAccountDto();
            responseRetrieveAccountDto.setStatus("OK");
            responseRetrieveAccountDto.setAccountBalance(account.getAmount());
            return responseRetrieveAccountDto;
        }
    }
}
