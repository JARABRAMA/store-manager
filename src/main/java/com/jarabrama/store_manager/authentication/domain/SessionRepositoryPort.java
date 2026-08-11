package com.jarabrama.store_manager.authentication.domain;

import com.jarabrama.store_manager.authentication.domain.model.Session;
import com.jarabrama.store_manager.authentication.domain.model.SystemUser;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface SessionRepositoryPort {
  void revokeAllByUser(UUID userId);
  Session save(Session session);

  Optional<Session> findLastByUser(SystemUser user);

  void updateExpirationTimeout(UUID id, Instant newExpirationTimeout);

  void updateLastActivityAt(UUID id, Instant lastActivityAt);
}
