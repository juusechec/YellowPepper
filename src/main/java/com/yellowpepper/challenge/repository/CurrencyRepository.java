package com.yellowpepper.challenge.repository;
import com.yellowpepper.challenge.repository.model.Currency;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CurrencyRepository extends ReactiveCrudRepository<Currency, Integer> {
}
