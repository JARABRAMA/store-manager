package com.jarabrama.store_manager.authentication.persistence.mappers;

import com.jarabrama.store_manager.authentication.domain.model.AuthToken;
import com.jarabrama.store_manager.authentication.persistence.entities.AuthTokenEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthTokenEntityMapper {
  public AuthTokenEntity toEntity(AuthToken authToken) {
    return AuthTokenEntity.builder()
            .id(authToken.getId())
            .tokenHash(authToken.getTokenHash())
            .tokenType(authToken.getTokenType())
            .revoked(authToken.isRevoked())
            .createdAt(authToken.getCreatedAt())
            .expiresAt(authToken.getExpiresAt())
            .build();
  }

  public AuthToken toDomain(AuthTokenEntity authTokenEntity) {
    return AuthToken.builder()
            .id(authTokenEntity.getId())
            .userId(authTokenEntity.getId())
            .tokenHash(authTokenEntity.getTokenHash())
            .tokenType(authTokenEntity.getTokenType())
            .revoked(authTokenEntity.isRevoked())
            .createdAt(authTokenEntity.getCreatedAt())
            .expiresAt(authTokenEntity.getExpiresAt())
            .build();
  }
}
