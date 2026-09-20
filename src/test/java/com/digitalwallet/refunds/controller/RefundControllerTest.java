package com.digitalwallet.refunds.controller;

import com.digitalwallet.refunds.dto.RefundRequest;
import com.digitalwallet.refunds.dto.RefundResponse;
import com.digitalwallet.refunds.service.RefundService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest(RefundController.class)
class RefundControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private RefundService refundService;

    private RefundResponse buildMockResponse(Long id, String status) {
        RefundResponse response = new RefundResponse();
        response.setId(id);
        response.setWalletId("WAL-123456");
        response.setExternalTransactionId("TXN-987654");
        response.setAmount(new BigDecimal("150.00"));
        response.setCurrency("MXN");
        response.setStatus(status);
        response.setReason("Cobro duplicado");
        response.setCreatedAt(LocalDateTime.now());
        return response;
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    void shouldGetAllRefundsWithPagination() {
        RefundResponse response = buildMockResponse(1L, "PENDING");

        Mockito.when(refundService.getAllRefunds())
                .thenReturn(Flux.just(response));

        webTestClient.get()
                .uri("/api/v1/refunds?page=0&size=10")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RefundResponse.class)
                .hasSize(1);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    void shouldCreateRefund() {
        RefundRequest request = new RefundRequest();
        request.setWalletId("WAL-123456");
        request.setExternalTransactionId("TXN-987654");
        request.setAmount(new BigDecimal("150.00"));
        request.setCurrency("MXN");
        request.setReason("Cobro duplicado");

        RefundResponse response = buildMockResponse(1L, "PENDING");

        Mockito.when(refundService.processRefund(any(RefundRequest.class)))
                .thenReturn(Mono.just(response));

        webTestClient.mutateWith(csrf()) // 👈 Evita el error 403 por CSRF en WebFlux
                .post()
                .uri("/api/v1/refunds")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.walletId").isEqualTo("WAL-123456");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    void shouldUpdateRefundStatus() {
        RefundResponse response = buildMockResponse(1L, "COMPLETED");

        Mockito.when(refundService.updateStatus(eq(1L), eq("COMPLETED")))
                .thenReturn(Mono.just(response));

        webTestClient.mutateWith(csrf()) // 👈 Evita el error 403 por CSRF en WebFlux
                .patch()
                .uri("/api/v1/refunds/1/status?status=COMPLETED")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("COMPLETED");
    }
}