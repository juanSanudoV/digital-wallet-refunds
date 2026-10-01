package com.digitalwallet.refunds.client;

import com.digitalwallet.refunds.dto.ProductDto;
import com.digitalwallet.refunds.dto.ProductResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ProductClientImpl implements ProductClient {

    private final WebClient webClient;

    public ProductClientImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8081").build();
    }

    @Override
    public Mono<ProductDto> getProductBySku(String sku) {
        return existsBySku(sku)
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        ProductDto dto = new ProductDto();
                        dto.setSku(sku);
                        return Mono.just(dto);
                    }
                    return Mono.empty();
                });
    }

    @Override
    public Flux<ProductResponse> getAllProducts() {
        return this.webClient.get()
                .uri("/internal/v1/products")
                .retrieve()
                .bodyToFlux(ProductResponse.class);
    }

    @Override
    public Mono<ProductResponse> getProductById(Long id) {
        return this.webClient.get()
                .uri("/internal/v1/products/{id}", id)
                .retrieve()
                .bodyToMono(ProductResponse.class);
    }

    @Override
    public Mono<Boolean> existsBySku(String sku) {
        return this.webClient.get()
                .uri("/internal/v1/products/exists-by-sku/{sku}", sku)
                .retrieve()
                .bodyToMono(Boolean.class);
    }
}