package com.digitalwallet.refunds.client;

import com.digitalwallet.refunds.dto.ProductResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ProductClient {

    private final WebClient webClient;

    public ProductClient(WebClient.Builder builder, 
                         @Value("${persistence.service.url:http://localhost:8081}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    // 1. Obtener producto por SKU
    @CircuitBreaker(name = "persistenceServiceCB", fallbackMethod = "getProductBySkuFallback")
    public Mono<ProductResponse> getProductBySku(String sku) {
        return webClient.get()
                .uri("/api/v1/products/{sku}", sku)
                .retrieve()
                .bodyToMono(ProductResponse.class);
    }

    // 2. Obtener todos los productos
    @CircuitBreaker(name = "persistenceServiceCB", fallbackMethod = "getAllProductsFallback")
    public Flux<ProductResponse> getAllProducts() {
        return webClient.get()
                .uri("/api/v1/products")
                .retrieve()
                .bodyToFlux(ProductResponse.class);
    }

    // 3. Obtener producto por ID
    @CircuitBreaker(name = "persistenceServiceCB", fallbackMethod = "getProductByIdFallback")
    public Mono<ProductResponse> getProductById(Long id) {
        return webClient.get()
                .uri("/api/v1/products/id/{id}", id)
                .retrieve()
                .bodyToMono(ProductResponse.class);
    }

    // 4. Verificar si existe por SKU
    @CircuitBreaker(name = "persistenceServiceCB", fallbackMethod = "existsBySkuFallback")
    public Mono<Boolean> existsBySku(String sku) {
        return webClient.get()
                .uri("/api/v1/products/exists/{sku}", sku)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    // ==========================================
    // MÉTODOS FALLBACK
    // ==========================================

    public Mono<ProductResponse> getProductBySkuFallback(String sku, Throwable throwable) {
        return Mono.error(new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE, 
                "El catálogo de productos no está disponible temporalmente. Intente más tarde."
        ));
    }

    public Flux<ProductResponse> getAllProductsFallback(Throwable throwable) {
        return Flux.empty();
    }

    public Mono<ProductResponse> getProductByIdFallback(Long id, Throwable throwable) {
        return Mono.error(new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE, 
                "No fue posible consultar el producto por ID debido a una falla en el servicio remoto."
        ));
    }

    public Mono<Boolean> existsBySkuFallback(String sku, Throwable throwable) {
        return Mono.error(new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE, 
                "El catálogo de productos no está disponible para validar el SKU."
        ));
    }
}