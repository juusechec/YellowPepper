package com.yellowpepper.challenge.service;

import com.yellowpepper.challenge.dto.RequestCreateTransactionDto;
import com.yellowpepper.challenge.dto.ResponseCreateTransactionDto;
import com.yellowpepper.challenge.exception.AccountTransfersLimitExceedException;
import com.yellowpepper.challenge.exception.CurrencyNotSupportedException;
import com.yellowpepper.challenge.exception.InsufficientFundsException;
import com.yellowpepper.challenge.exception.WrongInformationException;
import com.yellowpepper.challenge.repository.AccountRepository;
import com.yellowpepper.challenge.repository.CurrencyRepository;
import com.yellowpepper.challenge.repository.TransferRepository;
import com.yellowpepper.challenge.repository.model.Account;
import com.yellowpepper.challenge.repository.model.Currency;
import com.yellowpepper.challenge.repository.model.Transfer;
import com.yellowpepper.challenge.service.enums.AvailabilityTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class TransferService {
  @Autowired AccountRepository accountRepository;

  @Autowired CurrencyRepository currencyRepository;

  @Autowired TransferRepository transferRepository;

  @Autowired DroolsService droolsService;

  @Autowired ExchangeService exchangeService;

  private static final Logger LOGGER = Logger.getLogger(TransferService.class.getName());

  public Mono<ResponseCreateTransactionDto> createTransfer(
      RequestCreateTransactionDto requestCreateTransactionDto) {
    String abbreviationCurrency = requestCreateTransactionDto.getCurrency();

    if (abbreviationCurrency.equals("USD")) {
      Flux<Currency> currencyFlux =
          currencyRepository.getCurrencyByAbbreviation(abbreviationCurrency);
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

      Mono<Integer> numTodayTransactionsFlux =
          accountRepository.getNumTodayTransactions(idAccountOrigin).next();

      return Mono.zip(
              accountOriginMono, accountDestinationMono, transferMono, numTodayTransactionsFlux)
          .flatMap(
              data -> {
                Account accountOrigin = data.getT1();
                Account accountDestination = data.getT2();
                Transfer transferStarted = data.getT3();
                Integer numTodayTransactions = data.getT4();
                return discountTransfer(
                    accountOrigin, accountDestination, transferStarted, numTodayTransactions);
              })
          .switchIfEmpty(
              Mono.defer(
                  () -> {
                    throw new WrongInformationException("Wrong parameters");
                  }));
    } else {
      throw new CurrencyNotSupportedException(
          String.format("Currency %s not supported", abbreviationCurrency));
    }
  }

  public Mono<ResponseCreateTransactionDto> discountTransfer(
      Account origin, Account destiny, Transfer transfer, Integer numTodayTransactions) {
    LOGGER.log(Level.INFO, "BEFORE DISCOUNT ORIGIN: {0}", origin);
    LOGGER.log(Level.INFO, "BEFORE DISCOUNT DESTINATION: {0}", destiny);
    BigDecimal amountToTransfer = transfer.getAmount();
    BigDecimal accountAmount = origin.getAmount();
    BigDecimal tax = BigDecimal.valueOf(droolsService.getTax(amountToTransfer));
    Integer timesAccountTransaction = numTodayTransactions;
    AvailabilityTransfer availability =
        droolsService.getTransferAvailability(
            amountToTransfer, accountAmount, tax, timesAccountTransaction);

    if (availability != null && availability.equals(AvailabilityTransfer.NO_FUNDS)) {
      transfer.setStatus("INSUFFICIENT_FUNDS");
      Mono<Transfer> transferMono = transferRepository.save(transfer);
      return transferMono.map(
          a -> {
            LOGGER.warning("Updated state INSUFFICIENT_FUNDS");
            throw new InsufficientFundsException("insufficient-funds");
          });
    } else if (availability != null && availability.equals(AvailabilityTransfer.LIMITS_EXCEED)) {
      transfer.setStatus("LIMITS_EXCEED");
      Mono<Transfer> transferMono = transferRepository.save(transfer);
      return transferMono.map(
          a -> {
            LOGGER.warning("Updated state LIMITS_EXCEED");
            throw new AccountTransfersLimitExceedException("limit_exceeded");
          });
    } else {
      return applyDiscount(transfer, origin, destiny, tax, amountToTransfer);
    }
  }

  public Mono<ResponseCreateTransactionDto> applyDiscount(
      Transfer transfer, Account origin, Account destiny, BigDecimal tax, BigDecimal amountToTransfer) {
    Mono<Transfer> transferMono = transferRepository.save(transfer);

    return transferMono
        .flatMap(done -> convertCurrenciesToUSD(origin, destiny))
        .map(
            oldAmountsInUSD -> {
                BigDecimal originInUSD = oldAmountsInUSD.getNewAmountOfOrigin();
                BigDecimal destinyInUSD = oldAmountsInUSD.getNewAmountOfDestiny();
              return getNewAmounts(tax, amountToTransfer, originInUSD, destinyInUSD);
            })
        .flatMap(
            newAmountsInUSD ->
                convertCurrenciesToCAD(transfer, origin, destiny, newAmountsInUSD, tax))
        .flatMap(done -> exchangeService.fromUSDtoCAD(BigDecimal.valueOf(1.0)))
        .map(cadInUsd -> createResponse(tax, cadInUsd));
  }

  public Mono<NewAmounts> convertCurrenciesToUSD(Account origin, Account destiny) {
    if (origin.getIdCurrency() == 1 && destiny.getIdCurrency() == 2) {
      LOGGER.info("TRANSFORM TO USD -> CASE ORIGIN: USD, DESTINATION: CAD");
      Mono<BigDecimal> destinyInUSDMono =
          exchangeService.fromCADtoUSD(destiny.getAmount());
      return destinyInUSDMono.map(
          destinyInUSD -> {
            BigDecimal originInUSD = origin.getAmount();
            return new NewAmounts(originInUSD, destinyInUSD);
          });
    } else if (origin.getIdCurrency() == 2 && destiny.getIdCurrency() == 2) {
      LOGGER.info("TRANSFORM TO USD -> CASE ORIGIN: CAD, DESTINATION: CAD");
      Mono<BigDecimal> originInUSDMono =
          exchangeService.fromCADtoUSD(origin.getAmount());
      Mono<BigDecimal> destinyInUSDMono =
          exchangeService.fromCADtoUSD(destiny.getAmount());
      return Mono.zip(originInUSDMono, destinyInUSDMono)
          .map(
              data -> {
                BigDecimal originInUSD = data.getT1();
                BigDecimal destinyInUSD = data.getT2();
                return new NewAmounts(originInUSD, destinyInUSD);
              });
    } else if (origin.getIdCurrency() == 2 && destiny.getIdCurrency() == 1) {
      LOGGER.info("TRANSFORM TO USD -> CASE ORIGIN: CAD, DESTINATION: USD");
      Mono<BigDecimal> originInUSDMono = exchangeService.fromCADtoUSD(origin.getAmount());
      return originInUSDMono.map(
          originInUSD -> {
            BigDecimal destinyInUSD = destiny.getAmount();
            return new NewAmounts(originInUSD, destinyInUSD);
          });
    } else {
      LOGGER.info("TRANSFORM TO USD -> CASE ORIGIN: USD, DESTINATION: USD");
      BigDecimal originInUSD = origin.getAmount();
      BigDecimal destinyInUSD = destiny.getAmount();
      return Mono.just(new NewAmounts(originInUSD, destinyInUSD));
    }
  }

  public Mono<Boolean> convertCurrenciesToCAD(
      Transfer transfer, Account origin, Account destiny, NewAmounts newAmountsInUSD, BigDecimal tax) {
      BigDecimal newAmountOfOriginInUSD = newAmountsInUSD.getNewAmountOfOrigin();
      BigDecimal newAmountOfDestinyInUSD = newAmountsInUSD.getNewAmountOfDestiny();
    if (origin.getIdCurrency() == 1 && destiny.getIdCurrency() == 2) {
      LOGGER.info("TRANSFORM TO ACCOUNT CURRENCY -> CASE ORIGIN: USD, DESTINATION: CAD");
        BigDecimal newAmountOfOrigin = newAmountOfOriginInUSD;
      Mono<BigDecimal> destinyInCADMono = exchangeService.fromUSDtoCAD(newAmountOfDestinyInUSD);
      return destinyInCADMono.flatMap(
          destinyInCAD -> {
              BigDecimal newAmountOfDestiny = destinyInCAD;
            return saveToDB(origin, destiny, transfer, newAmountOfOrigin, newAmountOfDestiny, tax);
          });
    } else if (origin.getIdCurrency() == 2 && destiny.getIdCurrency() == 2) {
      LOGGER.info("TRANSFORM TO ACCOUNT CURRENCY -> CASE ORIGIN: CAD, DESTINATION: CAD");
      Mono<BigDecimal> originInCADMono = exchangeService.fromUSDtoCAD(newAmountOfOriginInUSD);
      Mono<BigDecimal> destinyInCADMono = exchangeService.fromUSDtoCAD(newAmountOfDestinyInUSD);
      return Mono.zip(originInCADMono, destinyInCADMono)
          .flatMap(
              data -> {
                  BigDecimal newAmountOfOrigin = data.getT1();
                  BigDecimal newAmountOfDestiny = data.getT2();
                return saveToDB(
                    origin, destiny, transfer, newAmountOfOrigin, newAmountOfDestiny, tax);
              });
    } else if (origin.getIdCurrency() == 2 && destiny.getIdCurrency() == 1) {
      LOGGER.info("TRANSFORM TO ACCOUNT CURRENCY -> CASE ORIGIN: CAD, DESTINATION: USD");
      BigDecimal newAmountOfDestiny = newAmountOfDestinyInUSD;
      Mono<BigDecimal> originInCADMono = exchangeService.fromUSDtoCAD(newAmountOfOriginInUSD);
      return originInCADMono.flatMap(
          originInCAD -> {
            BigDecimal newAmountOfOrigin = originInCAD;
            return saveToDB(origin, destiny, transfer, newAmountOfOrigin, newAmountOfDestiny, tax);
          });
    } else {
      LOGGER.info("TRANSFORM TO ACCOUNT CURRENCY -> CASE ORIGIN: USD, DESTINATION: USD");
      BigDecimal newAmountOfOrigin = newAmountOfOriginInUSD;
        BigDecimal newAmountOfDestiny = newAmountOfDestinyInUSD;
      return saveToDB(origin, destiny, transfer, newAmountOfOrigin, newAmountOfDestiny, tax);
    }
  }

  public Mono<Boolean> saveToDB(
      Account origin,
      Account destiny,
      Transfer transfer,
      BigDecimal newAmountOfOrigin,
      BigDecimal newAmountOfDestiny,
      BigDecimal tax) {
    origin.setAmount(newAmountOfOrigin);
    Mono<Account> originMono = accountRepository.save(origin);

    destiny.setAmount(newAmountOfDestiny);
    Mono<Account> destinyMono = accountRepository.save(destiny);

    transfer.setStatus("DONE");
    transfer.setTax(tax);
    Mono<Transfer> transferMono = transferRepository.save(transfer);

    return Mono.zip(originMono, destinyMono, transferMono)
        .map(
            data -> {
              LOGGER.log(Level.INFO, "AFTER DISCOUNT ORIGIN: {0}", data.getT1());
              LOGGER.log(Level.INFO, "AFTER DISCOUNT DESTINATION: {0}", data.getT2());
              LOGGER.log(Level.INFO, "AFTER DISCOUNT TRANSFER: {0}", data.getT3());
              return true;
            });
  }

  final class NewAmounts {
    private final BigDecimal newAmountOfOrigin;
    private final BigDecimal newAmountOfDestiny;

    public NewAmounts(BigDecimal newAmountOfOrigin, BigDecimal newAmountOfDestiny) {
      this.newAmountOfOrigin = newAmountOfOrigin;
      this.newAmountOfDestiny = newAmountOfDestiny;
    }

    public BigDecimal getNewAmountOfOrigin() {
      return newAmountOfOrigin;
    }

    public BigDecimal getNewAmountOfDestiny() {
      return newAmountOfDestiny;
    }
  }

  public NewAmounts getNewAmounts(
          BigDecimal tax, BigDecimal amountToTransfer, BigDecimal originAmount, BigDecimal destinyAmount) {
      BigDecimal taxValue = amountToTransfer.multiply(tax.divide(BigDecimal.valueOf(100)));
      BigDecimal newAmountOfOrigin = originAmount.subtract(amountToTransfer).subtract(taxValue);
      BigDecimal newAmountOfDestiny = destinyAmount.add(amountToTransfer);
    return new NewAmounts(newAmountOfOrigin, newAmountOfDestiny);
  }

  public ResponseCreateTransactionDto createResponse(BigDecimal tax, BigDecimal cad) {
    ResponseCreateTransactionDto responseCreateTransactionDto = new ResponseCreateTransactionDto();
    responseCreateTransactionDto.setStatus("OK");
    responseCreateTransactionDto.setTaxCollected(tax);
    responseCreateTransactionDto.setCanadianExchange(cad);
    return responseCreateTransactionDto;
  }
}
