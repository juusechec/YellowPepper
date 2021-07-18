package com.yellowpepper.challenge.service;

import com.yellowpepper.challenge.dto.RequestCreateTransactionDto;
import com.yellowpepper.challenge.dto.ResponseCreateTransactionDto;
import com.yellowpepper.challenge.exception.CurrencyNotSupportedException;
import com.yellowpepper.challenge.exception.InsufficientFundsException;
import com.yellowpepper.challenge.repository.AccountRepository;
import com.yellowpepper.challenge.repository.CurrencyRepository;
import com.yellowpepper.challenge.repository.TransferRepository;
import com.yellowpepper.challenge.repository.model.Account;
import com.yellowpepper.challenge.repository.model.Currency;
import com.yellowpepper.challenge.repository.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransferService {
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    CurrencyRepository currencyRepository;

    @Autowired
    TransferRepository transferRepository;

    public Mono<ResponseCreateTransactionDto> createTransfer(RequestCreateTransactionDto requestCreateTransactionDto) {
        String abbreviationCurrency = requestCreateTransactionDto.getCurrency();

        if (abbreviationCurrency.equals("USD")) {
            Flux<Currency> currencyFlux = currencyRepository.getCurrencyByAbbreviation(abbreviationCurrency);
            Currency currency = currencyFlux.blockFirst();

            BigDecimal amount = requestCreateTransactionDto.getAmount();
            Integer idAccountOrigin = requestCreateTransactionDto.getOriginAccount();
            Integer idAccountDestination = requestCreateTransactionDto.getOriginAccount();

            Transfer newTransfer = new Transfer();
            newTransfer.setAmount(amount);
            newTransfer.setDatetime(LocalDateTime.now());
            newTransfer.setIdCurrency(currency.getId());
            newTransfer.setIdOriginAccount(idAccountOrigin);
            newTransfer.setIdDestinationAccount(idAccountDestination);
            newTransfer.setStatus("IN-PROGRESS");

            Mono<Transfer> transferMono = transferRepository.save(newTransfer);

            Mono<Account> accountOriginMono = accountRepository.findById(idAccountOrigin);

            Mono<Account> accountDestinationMono = accountRepository.findById(idAccountDestination);

            Mono<Integer> numTodayTransactionsFlux =  accountRepository.getNumTodayTransactions(idAccountOrigin).next();

            return Mono.zip(accountOriginMono, accountDestinationMono, transferMono, numTodayTransactionsFlux).flatMap(data->{
                String a = discountTransfer(data.getT1(), data.getT2(), data.getT3(), data.getT4());
                ResponseCreateTransactionDto response = createResponse(a);
                return Mono.just(response);
            });
        } else {
            throw new CurrencyNotSupportedException(String.format("Currency %s not supported", abbreviationCurrency));
        }
    }

    public String discountTransfer(Account origin, Account destiny, Transfer transfer, Integer numTodayTransactions) {
        BigDecimal amount = transfer.getAmount();
        if (origin.getAmount().compareTo(amount) > 0) {
            throw new InsufficientFundsException("insufficient-funds");
        } else if (numTodayTransactions > 3) {
            throw new InsufficientFundsException("limit_exceeded");
        } else {
            return "OK";
        }
    }

    public ResponseCreateTransactionDto createResponse(String status) {
        ResponseCreateTransactionDto responseCreateTransactionDto = new ResponseCreateTransactionDto();
        responseCreateTransactionDto.setStatus(status);
        return responseCreateTransactionDto;
    }
}
