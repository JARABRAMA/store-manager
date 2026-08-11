package com.jarabrama.store_manager.authentication.presentation.dto;

import lombok.Builder;

@Builder
public record LoginRequest(
        String username,
        String password,
        String deviceHash,
        boolean trustedDevice
) {
}
