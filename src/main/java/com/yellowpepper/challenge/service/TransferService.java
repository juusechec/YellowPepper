package com.yellowpepper.challenge.service;

import com.yellowpepper.challenge.domain.TaxTransferRequest;
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
import com.yellowpepper.challenge.service.enums.AvailabilityTransfer;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.time.LocalDateTime;

@Service
public class TransferService {
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    CurrencyRepository currencyRepository;

    @Autowired
    TransferRepository transferRepository;

    @Autowired
    DroolsService droolsService;

    public Mono<ResponseCreateTransactionDto> createTransfer(RequestCreateTransactionDto requestCreateTransactionDto) {
        String abbreviationCurrency = requestCreateTransactionDto.getCurrency();

        if (abbreviationCurrency.equals("USD")) {
            Flux<Currency> currencyFlux = currencyRepository.getCurrencyByAbbreviation(abbreviationCurrency);
            Currency currency = currencyFlux.blockFirst();

            BigDecimal amount = requestCreateTransactionDto.getAmount();
            Integer idAccountOrigin = requestCreateTransactionDto.getOriginAccount();
            Integer idAccountDestination = requestCreateTransactionDto.getDestinationAccount();

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

            Mono<Integer> numTodayTransactionsFlux = accountRepository.getNumTodayTransactions(idAccountOrigin).next();

            return Mono.zip(accountOriginMono, accountDestinationMono, transferMono, numTodayTransactionsFlux).flatMap(data -> {
                return discountTransfer(data.getT1(), data.getT2(), data.getT3(), data.getT4());
            });
        } else {
            throw new CurrencyNotSupportedException(String.format("Currency %s not supported", abbreviationCurrency));
        }
    }

    public Mono<ResponseCreateTransactionDto> discountTransfer(Account origin, Account destiny, Transfer transfer, Integer numTodayTransactions) {
        BigDecimal amountToTransfer = transfer.getAmount();
        BigDecimal accountAmount = origin.getAmount();
        Double tax = droolsService.getTax(amountToTransfer);
        Integer timesAccountTransaction = numTodayTransactions;
        AvailabilityTransfer availability = droolsService.getTransferAvailability(amountToTransfer, accountAmount, tax, timesAccountTransaction);

        if (availability != null && availability.equals(AvailabilityTransfer.NO_FUNDS)) {
            transfer.setStatus("INSUFFICIENT_FUNDS");
            Mono<Transfer> transferMono = transferRepository.save(transfer);
            return transferMono.map(a -> {
                System.out.println("Updated state INSUFFICIENT_FUNDS");
                throw new InsufficientFundsException("insufficient-funds");
            });
        } else if (availability != null && availability.equals(AvailabilityTransfer.LIMITS_EXCEED)) {
            transfer.setStatus("LIMITS_EXCEED");
            Mono<Transfer> transferMono = transferRepository.save(transfer);
            return transferMono.map(a -> {
                System.out.println("Updated state LIMITS_EXCEED");
                throw new InsufficientFundsException("limit_exceeded");
            });
        } else {
            Double taxValue = origin.getAmount().doubleValue() * tax / 100;
            Double newAmountOfOrigin = origin.getAmount().doubleValue() - taxValue;
            origin.setAmount(new BigDecimal(newAmountOfOrigin, MathContext.DECIMAL64));
            Mono<Account> originMono = accountRepository.save(origin);

            Double newAmountOfDestiny = destiny.getAmount().doubleValue() + amountToTransfer.doubleValue();
            destiny.setAmount(new BigDecimal(newAmountOfDestiny, MathContext.DECIMAL64));
            Mono<Account> destinyMono = accountRepository.save(destiny);

            transfer.setStatus("DONE");
            transfer.setTax(new BigDecimal(tax));
            Mono<Transfer> transferMono = transferRepository.save(transfer);

            return Mono.zip(originMono, destinyMono, transferMono).map(a -> createResponse(new BigDecimal(tax)));
        }
    }

    public ResponseCreateTransactionDto createResponse(BigDecimal tax) {
        ResponseCreateTransactionDto responseCreateTransactionDto = new ResponseCreateTransactionDto();
        responseCreateTransactionDto.setStatus("OK");
        responseCreateTransactionDto.setTaxCollected(tax);
        return responseCreateTransactionDto;
    }
}
