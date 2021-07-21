package com.yellowpepper.challenge.repository;

import com.yellowpepper.challenge.repository.model.Account;
import com.yellowpepper.challenge.repository.model.Customer;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {
  @Query("SELECT * FROM account WHERE id_holder = :idCustomer")
  Flux<Account> getCustomerAccounts(Integer idCustomer);
}
