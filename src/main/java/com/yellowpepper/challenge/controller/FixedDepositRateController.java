package com.yellowpepper.challenge.controller;

import com.yellowpepper.challenge.domain.TaxTransferRequest;
import com.yellowpepper.challenge.dto.RequestCreateTransactionDto;
import com.yellowpepper.challenge.dto.RequestRetrieveAccountDto;
import com.yellowpepper.challenge.dto.ResponseCreateTransactionDto;
import com.yellowpepper.challenge.dto.ResponseRetrieveAccountDto;
import com.yellowpepper.challenge.service.AccountService;
import com.yellowpepper.challenge.service.TransferService;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
public class FixedDepositRateController {

    @Autowired
    AccountService retrieveAccountService;

    @Autowired
    TransferService transferService;

    @RequestMapping(value = "/v1/customers/130303/retrieve-account", method = RequestMethod.POST, produces = "application/json")
    public ResponseRetrieveAccountDto getBook(@RequestBody RequestRetrieveAccountDto requestRetrieveAccountDto) {
        return retrieveAccountService.getResponseBody(requestRetrieveAccountDto);
    }

    @RequestMapping(value = "/v1/transactions", method = RequestMethod.POST, produces = "application/json")
    public Mono<ResponseCreateTransactionDto> createTransactions(@RequestBody RequestCreateTransactionDto requestCreateTransactionDto) {
        return transferService.createTransfer(requestCreateTransactionDto);
    }
}
