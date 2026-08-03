package com.jarabrama.store_manager.authentication.service.model;

import com.jarabrama.store_manager.authentication.domain.model.AuthTokenType;
import com.jarabrama.store_manager.authentication.domain.model.SystemRole;
import lombok.Builder;

import java.time.Duration;
import java.util.UUID;

@Builder
public record NewJwtTokenRequest(String username, SystemRole userRole, UUID sessionId, UUID trustedDeviceId,
                                 AuthTokenType tokenType, Duration expirationTimeout) {
}
