package com.yellowpepper.challenge.repository;
import com.yellowpepper.challenge.repository.model.Account;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AccountRepository extends ReactiveCrudRepository<Account, Integer> {
}
