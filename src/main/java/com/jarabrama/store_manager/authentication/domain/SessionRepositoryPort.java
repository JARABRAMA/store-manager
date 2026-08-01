package com.jarabrama.store_manager.authentication.domain;

import com.jarabrama.store_manager.authentication.domain.model.Session;

import java.util.Optional;
import java.util.UUID;

public interface SessionRepositoryPort {
  void revokeAllByUser(UUID userId);
}
