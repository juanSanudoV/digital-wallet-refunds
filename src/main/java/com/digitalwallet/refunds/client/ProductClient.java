package com.digitalwallet.refunds.client;

import com.digitalwallet.refunds.dto.ProductDto;
import com.digitalwallet.refunds.dto.ProductResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductClient {
    Mono<ProductDto> getProductBySku(String sku);
    Flux<ProductResponse> getAllProducts();
    Mono<ProductResponse> getProductById(Long id);
    Mono<Boolean> existsBySku(String sku);
}