package com.digitalwallet.refunds.service;

import com.digitalwallet.refunds.client.ProductClient;
import com.digitalwallet.refunds.dto.RefundRequest;
import com.digitalwallet.refunds.dto.RefundResponse;
import com.digitalwallet.refunds.entity.Refund;
import com.digitalwallet.refunds.repository.RefundRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
        return processRefund(request);
    }

    @Override
    @CircuitBreaker(name = "persistenceServiceCB", fallbackMethod = "fallbackRefund")
    public Mono<RefundResponse> processRefund(RefundRequest request) {
        return productClient.getProductBySku(request.getSku())
                .flatMap(product -> saveRefund(request, "APPROVED"));
    }

    public Mono<RefundResponse> fallbackRefund(RefundRequest request, Throwable ex) {
        return Mono.error(new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "El servicio de persistencia no está disponible temporalmente"
        ));
    }

    @Override
    public Mono<RefundResponse> getRefundById(Long id) {
        return refundRepository.findById(id)
                .map(this::mapToResponse);
    }

    @Override
    public Flux<RefundResponse> getAllRefunds() {
        return refundRepository.findAll()
                .map(this::mapToResponse);
    }

    @Override
    public Mono<RefundResponse> updateStatus(Long id, String status) {
        return refundRepository.findById(id)
                .flatMap(refund -> {
                    refund.setStatus(status);
                    return refundRepository.save(refund);
                })
                .map(this::mapToResponse);
    }

    private Mono<RefundResponse> saveRefund(RefundRequest request, String status) {
        Refund refund = new Refund();
        refund.setSku(request.getSku());
        refund.setQuantity(request.getQuantity());
        refund.setWalletId(request.getWalletId());
        refund.setReason(request.getReason());
        refund.setStatus(status);
        refund.setAmount(new BigDecimal("99.99"));
        refund.setCurrency("MXN");
        refund.setCreatedAt(LocalDateTime.now());

        return refundRepository.save(refund)
                .map(this::mapToResponse);
    }

    private RefundResponse mapToResponse(Refund saved) {
        RefundResponse response = new RefundResponse();
        response.setId(saved.getId());
        response.setSku(saved.getSku());
        response.setQuantity(saved.getQuantity());
        response.setWalletId(saved.getWalletId());
        response.setStatus(saved.getStatus());
        response.setReason(saved.getReason());
        response.setAmount(saved.getAmount());
        response.setCurrency(saved.getCurrency());
        response.setCreatedAt(saved.getCreatedAt());
        return response;
    }
}