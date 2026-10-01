package com.digitalwallet.refunds.service;

import com.digitalwallet.refunds.client.ProductClient;
import com.digitalwallet.refunds.dto.ProductDto;
import com.digitalwallet.refunds.dto.RefundRequest;
import com.digitalwallet.refunds.dto.RefundResponse;
import com.digitalwallet.refunds.entity.Refund;
import com.digitalwallet.refunds.repository.RefundRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefundServiceTest {

    @Mock
    private ProductClient productClient;

    @Mock
    private RefundRepository refundRepository;

    private RefundServiceImpl refundService;

    @BeforeEach
    void setUp() {
        refundService = new RefundServiceImpl(productClient, refundRepository);
    }

    @Test
    void createRefund_Success() {
        RefundRequest request = new RefundRequest();
        request.setSku("KB-001");
        request.setQuantity(1);
        request.setWalletId("W-12345");
        request.setReason("Test");

        ProductDto productDto = new ProductDto();
        productDto.setSku("KB-001");

        Refund savedRefund = new Refund();
        savedRefund.setId(1L);
        savedRefund.setSku("KB-001");
        savedRefund.setQuantity(1);
        savedRefund.setWalletId("W-12345");
        savedRefund.setStatus("APPROVED");
        savedRefund.setAmount(new BigDecimal("99.99"));
        savedRefund.setCurrency("MXN");
        savedRefund.setCreatedAt(LocalDateTime.now());

        when(productClient.getProductBySku("KB-001")).thenReturn(Mono.just(productDto));
        when(refundRepository.save(any(Refund.class))).thenReturn(Mono.just(savedRefund));

        Mono<RefundResponse> result = refundService.createRefund(request);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getId().equals(1L) && "APPROVED".equals(response.getStatus()))
                .verifyComplete();
    }

    @Test
    void whenPersistenceServiceIsDown_thenFallbackShouldThrowException() {
        RefundRequest request = new RefundRequest();
        request.setSku("KB-001");

        Mono<RefundResponse> result = refundService.fallbackRefund(request, new RuntimeException("Service Down"));

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof ResponseStatusException &&
                        ((ResponseStatusException) throwable).getStatusCode().value() == 503)
                .verify();
    }
}