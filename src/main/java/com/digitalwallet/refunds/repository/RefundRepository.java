package com.digitalwallet.refunds.repository;

import com.digitalwallet.refunds.entity.Refund;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundRepository extends ReactiveCrudRepository<Refund, Long> {
}