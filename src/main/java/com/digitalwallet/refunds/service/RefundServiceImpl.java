package com.digitalwallet.refunds.service;

import com.digitalwallet.refunds.client.ProductClient;
import com.digitalwallet.refunds.dto.RefundRequest;
import com.digitalwallet.refunds.dto.RefundResponse;
import com.digitalwallet.refunds.entity.Refund;
import com.digitalwallet.refunds.repository.RefundRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class RefundServiceImpl implements RefundService {

    private static final Logger log = LoggerFactory.getLogger(RefundServiceImpl.class);

    private final ProductClient productClient;
    private final RefundRepository refundRepository;

    public RefundServiceImpl(ProductClient productClient, RefundRepository refundRepository) {
        this.productClient = productClient;
        this.refundRepository = refundRepository;
    }

    @Override
    @CircuitBreaker(name = "persistenceServiceCB", fallbackMethod = "fallbackRefund")
    public Mono<RefundResponse> createRefund(RefundRequest request) {
        log.info("Iniciando procesamiento de reembolso para SKU: {}", request.getSku());

        return productClient.getProductBySku(request.getSku())
                .doOnNext(prod -> log.info("Producto encontrado en client: {}", prod.getSku()))
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("productClient.getProductBySku devolvió MONO.EMPTY");
                    return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "El SKU no existe en el catálogo."));
                }))
                .flatMap(product -> saveRefund(request, "APPROVED"))
                .doOnError(err -> log.error("Error durante createRefund: ", err));
    }

    @Override
    public Mono<RefundResponse> processRefund(RefundRequest request) {
        return createRefund(request);
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

    public Mono<RefundResponse> fallbackRefund(RefundRequest request, Throwable t) {
        log.error("Fallback activado debido a: ", t);
        return Mono.error(new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "El catálogo de productos no está disponible para validar el SKU."
        ));
    }

    private Mono<RefundResponse> saveRefund(RefundRequest request, String status) {
        Refund refund = new Refund();
        refund.setId(null); // Garantiza que R2DBC ejecute un INSERT en la BD
        refund.setSku(request.getSku());
        refund.setQuantity(request.getQuantity());
        refund.setWalletId(request.getWalletId());
        refund.setReason(request.getReason());
        refund.setStatus(status);
        refund.setAmount(new BigDecimal("99.99"));
        refund.setCurrency("USD");
        refund.setCreatedAt(LocalDateTime.now());

        return refundRepository.save(refund)
                .doOnNext(saved -> log.info("Reembolso guardado exitosamente en DB con ID real: {}", saved.getId()))
                .doOnError(err -> log.error("Error al guardar en base de datos: ", err))
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
        response.setCreatedAt(saved.getCreatedAt());
        return response;
    }
}