package com.digitalwallet.refunds.service;

import com.digitalwallet.refunds.dto.RefundRequest;
import com.digitalwallet.refunds.dto.RefundResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RefundService {
    Mono<RefundResponse> createRefund(RefundRequest request);
    Mono<RefundResponse> processRefund(RefundRequest request);
    Mono<RefundResponse> updateStatus(Long id, String status);
    Flux<RefundResponse> getAllRefunds();
    Mono<RefundResponse> getRefundById(Long id);
}