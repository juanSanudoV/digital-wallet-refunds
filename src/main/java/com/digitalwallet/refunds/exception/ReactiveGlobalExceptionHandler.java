package com.digitalwallet.refunds.exception;

import com.digitalwallet.refunds.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Component
@Order(-2) // 👈 Prioridad alta para ejecutarse antes que el gestor de errores por defecto de WebFlux
public class ReactiveGlobalExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorMessage = "Error en la petición";

        if (ex instanceof WebExchangeBindException bindEx) {
            errorMessage = "Error de validación: " + bindEx.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
        } else if (ex instanceof RefundNotFoundException notFoundEx) {
            status = HttpStatus.NOT_FOUND;
            errorMessage = notFoundEx.getMessage();
        } else {
            errorMessage = ex.getMessage();
        }

        ErrorResponse errorResponse = new ErrorResponse(errorMessage, status.value());

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}