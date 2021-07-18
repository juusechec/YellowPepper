package com.yellowpepper.challenge.controller;

import com.yellowpepper.challenge.domain.TaxTransferRequest;
import com.yellowpepper.challenge.repository.BookRepository;
import com.yellowpepper.challenge.repository.model.Book;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;

@RestController
public class FixedDepositRateController {

    @Autowired BookRepository bookRepository;

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

    @RequestMapping(value = "/getBook", method = RequestMethod.GET, produces = "application/json")
    public Flux<Book> getBook() {
        return bookRepository.findAll();
    }
}
