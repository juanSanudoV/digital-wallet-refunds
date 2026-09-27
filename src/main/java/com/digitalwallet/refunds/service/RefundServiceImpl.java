package com.digitalwallet.refunds.service;

import com.digitalwallet.refunds.client.ProductClient;
import com.digitalwallet.refunds.dto.RefundRequest;
import com.digitalwallet.refunds.dto.RefundResponse;
import com.digitalwallet.refunds.entity.Refund;
import com.digitalwallet.refunds.repository.RefundRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefundServiceImpl implements RefundService {

    private final ProductClient productClient;
    private final RefundRepository refundRepository;

    public RefundServiceImpl(ProductClient productClient, RefundRepository refundRepository) {
        this.productClient = productClient;
        this.refundRepository = refundRepository;
    }

    @Override
    public Mono<RefundResponse> createRefund(RefundRequest request) {
        return productClient.existsBySku(request.getSku())
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new ResponseStatusException(
                                HttpStatus.BAD_REQUEST, 
                                "El producto con SKU " + request.getSku() + " no existe."
                        ));
                    }
                    return processRefund(request);
                });
    }

    @Override
    public Mono<RefundResponse> processRefund(RefundRequest request) {
        BigDecimal amount = request.getAmount() != null ? request.getAmount() : new BigDecimal("99.99");
        String currency = request.getCurrency() != null ? request.getCurrency() : "USD";

        Refund refundEntity = Refund.builder()
                .sku(request.getSku())
                .quantity(request.getQuantity())
                .walletId(request.getWalletId())
                .reason(request.getReason())
                .amount(amount)
                .currency(currency)
                .status("APPROVED")
                .createdAt(LocalDateTime.now())
                .build();

        return refundRepository.save(refundEntity)
                .map(this::mapToResponse);
    }

    @Override
    public Mono<RefundResponse> updateStatus(Long id, String status) {
        return refundRepository.findById(id)
                .flatMap(refund -> {
                    refund.setStatus(status);
                    return refundRepository.save(refund);
                })
                .map(this::mapToResponse)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND, 
                        "El reembolso con ID " + id + " no fue encontrado."
                )));
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
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND, 
                        "El reembolso con ID " + id + " no fue encontrado."
                )));
    }

    private RefundResponse mapToResponse(Refund entity) {
        String generatedRefundCode = "REF-" + (entity.getId() != null ? entity.getId() : UUID.randomUUID().toString().substring(0, 8));

        return new RefundResponse(
                entity.getId(),
                generatedRefundCode,
                entity.getQuantity(),
                entity.getSku(),
                entity.getWalletId(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getReason(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}