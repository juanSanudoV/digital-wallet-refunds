package com.digitalwallet.refunds.service;

import com.digitalwallet.refunds.dto.RefundRequest;
import com.digitalwallet.refunds.dto.RefundResponse;
import com.digitalwallet.refunds.entity.Refund;
import com.digitalwallet.refunds.exception.RefundNotFoundException;
import com.digitalwallet.refunds.repository.RefundRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class RefundServiceImpl implements RefundService {

    private final RefundRepository refundRepository;

    public RefundServiceImpl(RefundRepository refundRepository) {
        this.refundRepository = refundRepository;
    }

    @Override
    public Mono<RefundResponse> processRefund(RefundRequest request) {
        Refund refund = new Refund();
        refund.setWalletId(request.getWalletId());
        refund.setExternalTransactionId(request.getExternalTransactionId());
        refund.setAmount(request.getAmount());
        refund.setCurrency(request.getCurrency() != null ? request.getCurrency() : "MXN");
        refund.setReason(request.getReason());
        refund.setStatus("PENDING");
        refund.setCreatedAt(LocalDateTime.now());

        return refundRepository.save(refund)
                .map(this::mapToResponse);
    }

    @Override
    public Flux<RefundResponse> getAllRefunds() {
        return refundRepository.findAll()
                .map(this::mapToResponse);
    }

    @Override
    public Mono<RefundResponse> getRefundById(Long id) {
        return refundRepository.findById(id)
                .map(this::mapToResponse)
                .switchIfEmpty(Mono.error(new RefundNotFoundException(id)));
    }

    private RefundResponse mapToResponse(Refund refund) {
        RefundResponse response = new RefundResponse();
        response.setId(refund.getId());
        response.setWalletId(refund.getWalletId());
        response.setExternalTransactionId(refund.getExternalTransactionId());
        response.setAmount(refund.getAmount());
        response.setCurrency(refund.getCurrency());
        response.setReason(refund.getReason());
        response.setStatus(refund.getStatus());
        response.setCreatedAt(refund.getCreatedAt());
        return response;
    }
}