package com.yellowpepper.challenge.controller;

import com.yellowpepper.challenge.domain.TaxTransferRequest;
import com.yellowpepper.challenge.dto.RequestRetrieveAccountDto;
import com.yellowpepper.challenge.dto.ResponseRetrieveAccountDto;
import com.yellowpepper.challenge.repository.AccountRepository;
import com.yellowpepper.challenge.repository.model.Account;
import com.yellowpepper.challenge.service.RetrieveAccountService;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
public class FixedDepositRateController {

    @Autowired
    RetrieveAccountService retrieveAccountService;

    private final KieContainer kieContainer;

    public FixedDepositRateController(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    @RequestMapping(value = "/getTax", method = RequestMethod.GET, produces = "application/json")
    public TaxTransferRequest getQuestions(@RequestParam(required = true) BigDecimal amount) {
        KieSession kieSession = kieContainer.newKieSession();
        TaxTransferRequest taxTransferRequest = new TaxTransferRequest(amount);
        kieSession.insert(taxTransferRequest);
        kieSession.fireAllRules();
        kieSession.dispose();
        return taxTransferRequest;
    }

    @RequestMapping(value = "/v1/customers/130303/retrieve-account", method = RequestMethod.POST, produces = "application/json")
    public ResponseRetrieveAccountDto getBook(@RequestBody RequestRetrieveAccountDto requestRetrieveAccountDto) {
        return retrieveAccountService.getResponseBody(requestRetrieveAccountDto);
    }

    @RequestMapping(value = "/v1/transactions", method = RequestMethod.POST, produces = "application/json")
    public ResponseRetrieveAccountDto createTransactions(@RequestBody RequestRetrieveAccountDto requestRetrieveAccountDto) {
        return retrieveAccountService.getResponseBody(requestRetrieveAccountDto);
    }
}
