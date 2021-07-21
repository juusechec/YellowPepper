package com.yellowpepper.challenge.service;

import com.yellowpepper.challenge.exception.CustomerNotFoundException;
import com.yellowpepper.challenge.repository.CustomerRepository;
import com.yellowpepper.challenge.repository.model.Account;
import com.yellowpepper.challenge.repository.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CustomerService {
  @Autowired CustomerRepository customerRepository;

  public Mono<Customer> getCustomer(Integer customerId) {
    Mono<Customer> accountMono = customerRepository.findById(customerId);
    return accountMono.switchIfEmpty(
        Mono.defer(
            () -> {
              throw new CustomerNotFoundException(
                  String.format("User with id %s not found in the database", customerId));
            }));
  }

  public Flux<Account> getCustomerAccounts(Integer customerId) {
    Flux<Account> accountCustomerAccounts = customerRepository.getCustomerAccounts(customerId);
    return accountCustomerAccounts.switchIfEmpty(
        Mono.defer(
            () -> {
              throw new CustomerNotFoundException(
                  String.format("User with id %s not found in the database", customerId));
            }));
  }
}
