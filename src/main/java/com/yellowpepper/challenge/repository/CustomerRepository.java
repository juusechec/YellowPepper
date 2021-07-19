package com.yellowpepper.challenge.repository;

import com.yellowpepper.challenge.repository.model.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {
}
