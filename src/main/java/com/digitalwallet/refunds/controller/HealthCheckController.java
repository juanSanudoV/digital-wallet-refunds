package com.digitalwallet.refunds.controller;

import com.digitalwallet.refunds.dto.HealthCheckResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/healthcheck")
public class HealthCheckController {

    @GetMapping
    public Mono<HealthCheckResponse> getHealthStatus() {
        return Mono.just(new HealthCheckResponse(
                "UP",
                "digital-wallet-refunds",
                LocalDateTime.now()
        ));
    }
}