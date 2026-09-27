package com.digitalwallet.refunds.controller;

import com.digitalwallet.refunds.client.ProductClient;
import com.digitalwallet.refunds.dto.ProductResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/refunds/products")
public class RefundProductController {

    private final ProductClient productClient;

    public RefundProductController(ProductClient productClient) {
        this.productClient = productClient;
    }

    @GetMapping
    public Flux<ProductResponse> getAllProducts() {
        return productClient.getAllProducts();
    }

    @GetMapping("/{id}")
    public Mono<ProductResponse> getProductById(@PathVariable Long id) {
        return productClient.getProductById(id);
    }

    @GetMapping("/exists-sku/{sku}")
    public Mono<Boolean> checkSku(@PathVariable String sku) {
        return productClient.existsBySku(sku);
    }
}