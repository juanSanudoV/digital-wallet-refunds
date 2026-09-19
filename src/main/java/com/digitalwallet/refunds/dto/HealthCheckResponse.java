package com.digitalwallet.refunds.dto;

import java.time.LocalDateTime;

public record HealthCheckResponse(
    String status,
    String service,
    LocalDateTime timestamp
) {}