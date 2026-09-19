package com.digitalwallet.refunds.kafka;

import com.digitalwallet.refunds.dto.RefundEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RefundEventProducer {

    private static final Logger log = LoggerFactory.getLogger(RefundEventProducer.class);
    private static final String TOPIC = "wallet-refunds";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public RefundEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public Mono<Void> sendRefundEvent(RefundEvent event) {
        return Mono.fromRunnable(() -> {
            try {
                String jsonPayload = objectMapper.writeValueAsString(event);
                kafkaTemplate.send(TOPIC, event.walletId(), jsonPayload)
                        .whenComplete((result, ex) -> {
                            if (ex != null) {
                                log.warn("No se pudo enviar el evento a Kafka (Broker no disponible): {}", ex.getMessage());
                            } else {
                                log.info("Evento emitido a Kafka con éxito: {}", jsonPayload);
                            }
                        });
            } catch (Exception e) {
                log.error("Error al serializar el evento", e);
            }
        });
    }
}