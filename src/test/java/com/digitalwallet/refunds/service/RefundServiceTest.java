package com.digitalwallet.refunds.service;

import com.digitalwallet.refunds.client.ProductClient;
import com.digitalwallet.refunds.repository.RefundRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RefundServiceTest {

    @Mock
    private ProductClient productClient;

    @Mock
    private RefundRepository refundRepository;

    private RefundServiceImpl refundService;

    @BeforeEach
    void setUp() {
        // El constructor requiere primero ProductClient y segundo RefundRepository
        refundService = new RefundServiceImpl(productClient, refundRepository);
    }

    @Test
    void contextLoads() {
        // Prueba básica de inicialización de contexto
    }
}