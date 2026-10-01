package com.digitalwallet.refunds.controller;

import com.digitalwallet.refunds.dto.RefundRequest;
import com.digitalwallet.refunds.dto.RefundResponse;
import com.digitalwallet.refunds.service.RefundService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class RefundControllerTest {

    @Mock
    private RefundService refundService;

    @InjectMocks
    private RefundController refundController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenProcessRefund_thenShouldReturnApproved() {
        RefundRequest request = new RefundRequest();
        request.setSku("KB-001");
        request.setQuantity(1);
        request.setWalletId("W-98765");

        RefundResponse response = new RefundResponse();
        response.setStatus("APPROVED");

        when(refundService.createRefund(any(RefundRequest.class))).thenReturn(Mono.just(response));

        Mono<RefundResponse> result = refundController.createRefund(request);

        StepVerifier.create(result)
                .expectNextMatches(res -> "APPROVED".equals(res.getStatus()))
                .verifyComplete();
    }
}