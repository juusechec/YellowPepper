package com.yellowpepper.challenge.service;

import com.yellowpepper.challenge.domain.AccountTransferValidationRequest;
import com.yellowpepper.challenge.domain.TaxTransferRequest;
import com.yellowpepper.challenge.service.enums.AvailabilityTransfer;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DroolsService {
    private final KieContainer kieContainer;

    public DroolsService(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    public Double getTax(BigDecimal amount) {
        KieSession kieSession = kieContainer.newKieSession();
        TaxTransferRequest taxTransferRequest = new TaxTransferRequest(amount);
        kieSession.insert(taxTransferRequest);
        kieSession.fireAllRules();
        kieSession.dispose();
        System.out.println(taxTransferRequest.getTax());
        return taxTransferRequest.getTax();
    }

    public AvailabilityTransfer getTransferAvailability(BigDecimal amountToTransfer, BigDecimal accountAmount, Double tax, Integer timesAccountTransaction) {
        KieSession kieSession = kieContainer.newKieSession();
        AccountTransferValidationRequest accountTransferValidationRequest =
                new AccountTransferValidationRequest(amountToTransfer, accountAmount, tax, timesAccountTransaction);
        kieSession.insert(accountTransferValidationRequest);
        kieSession.fireAllRules();
        kieSession.dispose();
        System.out.println(accountTransferValidationRequest.getAmountToTransfer());
        return accountTransferValidationRequest.getAvailability();
    }
}
