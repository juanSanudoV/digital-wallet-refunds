package com.digitalwallet.refunds.service;

import com.digitalwallet.refunds.dto.RefundRequest;
import com.digitalwallet.refunds.dto.RefundResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RefundService {
    Mono<RefundResponse> processRefund(RefundRequest request);
    Flux<RefundResponse> getAllRefunds();
    Mono<RefundResponse> getRefundById(Long id);
}