package com.digitalwallet.refunds.service;

import com.digitalwallet.refunds.dto.RefundResponse;
import com.digitalwallet.refunds.entity.Refund;
import com.digitalwallet.refunds.repository.RefundRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefundServiceTest {

    @Mock
    private RefundRepository refundRepository;

    private RefundServiceImpl refundService;

    @BeforeEach
    void setUp() {
        refundService = new RefundServiceImpl(refundRepository);
    }

    @Test
    void getAllRefunds_ShouldReturnFluxOfRefundResponse() {
        Refund refund = new Refund();
        refund.setId(1L);
        refund.setWalletId("W123");
        refund.setAmount(new BigDecimal("100.00"));
        refund.setReason("Cancelación");
        refund.setStatus("COMPLETED");
        refund.setExternalTransactionId("TX123");
        refund.setCreatedAt(LocalDateTime.now());

        when(refundRepository.findAll()).thenReturn(Flux.just(refund));

        Flux<RefundResponse> result = refundService.getAllRefunds();

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getRefundById_ShouldReturnMonoOfRefundResponse() {
        Refund refund = new Refund();
        refund.setId(10L);
        refund.setWalletId("W123");
        refund.setAmount(new BigDecimal("50.00"));
        refund.setReason("Error de cobro");
        refund.setStatus("COMPLETED");
        refund.setExternalTransactionId("TX456");
        refund.setCreatedAt(LocalDateTime.now());

        when(refundRepository.findById(10L)).thenReturn(Mono.just(refund));

        Mono<RefundResponse> result = refundService.getRefundById(10L);

        StepVerifier.create(result)
                .expectNextMatches(response -> response != null)
                .verifyComplete();
    }
}