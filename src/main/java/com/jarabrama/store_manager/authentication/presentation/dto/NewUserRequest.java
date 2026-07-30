package com.jarabrama.store_manager.authentication.presentation.dto;

public record NewUserRequest(
        String username,
        String password
) {
}
