package com.jarabrama.store_manager.authentication.domain;

import com.jarabrama.store_manager.authentication.domain.model.AuthToken;

import java.time.Instant;
import java.util.UUID;

public interface AuthTokenRepositoryPort {
  AuthToken save(AuthToken authToken);
  void updateExpirationTimeout(UUID id, Instant newExpirationTimeout);
  void revokeAllByUser(UUID userId);
}
