package com.digitalwallet.refunds.service;

import com.digitalwallet.refunds.client.ProductClient;
import com.digitalwallet.refunds.dto.RefundRequest;
import com.digitalwallet.refunds.dto.RefundResponse;
import com.digitalwallet.refunds.repository.RefundRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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
    void whenPersistenceServiceIsDown_thenFallbackShouldReturn503() {
        RefundRequest request = new RefundRequest();
        request.setSku("KB-001");
        request.setQuantity(1);
        request.setWalletId("W-98765");
        request.setReason("Test Fallback");

        Mono<RefundResponse> result = refundService.fallbackRefund(request, new RuntimeException("Service Down"));

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof ResponseStatusException &&
                        ((ResponseStatusException) throwable).getStatusCode().value() == 503)
                .verify();
    }
}