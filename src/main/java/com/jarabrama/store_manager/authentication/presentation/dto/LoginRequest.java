package com.jarabrama.store_manager.authentication.presentation.dto;

public record LoginRequest(
        String username,
        String password
) {
}
