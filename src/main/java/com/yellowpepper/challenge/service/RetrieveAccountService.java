package com.yellowpepper.challenge.service;

import com.yellowpepper.challenge.dto.RequestRetrieveAccountDto;
import com.yellowpepper.challenge.dto.ResponseRetrieveAccountDto;
import com.yellowpepper.challenge.repository.AccountRepository;
import com.yellowpepper.challenge.repository.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RetrieveAccountService {
    @Autowired
    AccountRepository accountRepository;

    public ResponseRetrieveAccountDto getResponseBody(RequestRetrieveAccountDto requestRetrieveAccountDto) {
        Integer id = Integer.valueOf(requestRetrieveAccountDto.getAccount());
        Mono<Account> accountMono = accountRepository.findById(id);
        Account account = accountMono.block();
        ResponseRetrieveAccountDto responseRetrieveAccountDto = new ResponseRetrieveAccountDto();
        if (account == null) {
            responseRetrieveAccountDto.setStatus("ERROR");
            String[] errors = {"The request Account doesn't exits"};
            responseRetrieveAccountDto.setErrors(errors);
        } else {
            responseRetrieveAccountDto.setStatus("OK");
            responseRetrieveAccountDto.setAccountBalance(account.getAmount());
            String[] errors = {};
            responseRetrieveAccountDto.setErrors(errors);
        }
        return responseRetrieveAccountDto;
    }
}
