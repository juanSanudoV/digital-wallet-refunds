package com.digitalwallet.refunds.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${persistence.service.url:http://localhost:8081}")
    private String persistenceServiceUrl;

    @Bean
    public WebClient persistenceWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(persistenceServiceUrl)
                .build();
    }
}