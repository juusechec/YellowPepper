package com.yellowpepper.challenge.repository;
import com.yellowpepper.challenge.repository.model.Account;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface AccountRepository extends ReactiveCrudRepository<Account, Integer> {
    @Query("SELECT count(*) FROM transfer WHERE id_origin_account = :idOriginAccount AND datetime >= CAST(current_timestamp AS DATE)")
    public Flux<Integer> getNumTodayTransactions(Integer idOriginAccount);
}
