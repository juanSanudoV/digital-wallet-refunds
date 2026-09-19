package com.digitalwallet.refunds.repository;

import com.digitalwallet.refunds.entity.Refund;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface RefundRepository extends ReactiveCrudRepository<Refund, Long> {

    @Query("SELECT id, wallet_id, external_transaction_id, amount, currency, reason, status, created_at FROM refunds")
    Flux<Refund> findAll();
}