package com.yellowpepper.challenge.repository;

import com.yellowpepper.challenge.repository.model.Transfer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TransferRepository extends ReactiveCrudRepository<Transfer, Integer> {
}
