package com.digitalwallet.refunds.controller;

import com.digitalwallet.refunds.dto.RefundRequest;
import com.digitalwallet.refunds.dto.RefundResponse;
import com.digitalwallet.refunds.service.RefundService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/refunds")
public class RefundController {

    private final RefundService refundService;

    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    @GetMapping
    public Flux<RefundResponse> getAllRefunds() {
        return refundService.getAllRefunds();
    }

    @GetMapping("/{id}")
    public Mono<RefundResponse> getRefundById(@PathVariable Long id) {
        return refundService.getRefundById(id);
    }

    @PostMapping
    public Mono<RefundResponse> createRefund(@RequestBody RefundRequest request) {
        return refundService.processRefund(request);
    }

    @PatchMapping("/{id}/status")
    public Mono<RefundResponse> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return refundService.updateStatus(id, status);
    }
}