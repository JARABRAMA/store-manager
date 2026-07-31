package com.jarabrama.store_manager.authentication.presentation.dto;

public record LoginResponse(
        String authToken,
        String refreshToken
) {
}
