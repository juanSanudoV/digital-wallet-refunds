package com.digitalwallet.refunds.controller;

import com.digitalwallet.refunds.dto.RefundResponse;
import com.digitalwallet.refunds.service.RefundService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest(RefundController.class)
@AutoConfigureWebTestClient
@WithMockUser
class RefundControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private RefundService refundService;

    @Test
    void getAllRefunds_ShouldReturnOk() {
        when(refundService.getAllRefunds()).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/api/v1/refunds")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getRefundById_ShouldReturnOk() {
        RefundResponse mockResponse = Mockito.mock(RefundResponse.class);
        when(refundService.getRefundById(10L)).thenReturn(Mono.just(mockResponse));

        webTestClient.get()
                .uri("/api/v1/refunds/10")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void processRefund_ShouldReturnOk() {
        RefundResponse mockResponse = Mockito.mock(RefundResponse.class);
        when(refundService.processRefund(any())).thenReturn(Mono.just(mockResponse));

        String validJson = """
                {
                    "walletId": "W-123",
                    "amount": 100.00,
                    "reason": "Prueba de refund",
                    "externalTransactionId": "TXN-001"
                }
                """;

        webTestClient
                .mutateWith(csrf())
                .post()
                .uri("/api/v1/refunds")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validJson)
                .exchange()
                .expectStatus().isOk();
    }
}