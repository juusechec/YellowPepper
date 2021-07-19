package com.yellowpepper.challenge.repository;

import com.yellowpepper.challenge.repository.model.Currency;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CurrencyRepository extends ReactiveCrudRepository<Currency, Integer> {
  @Query("SELECT * FROM currency WHERE abbreviation = :abbreviation")
  Flux<Currency> getCurrencyByAbbreviation(String abbreviation);
}
