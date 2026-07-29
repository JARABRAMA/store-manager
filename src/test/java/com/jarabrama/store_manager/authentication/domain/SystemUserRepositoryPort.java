package com.jarabrama.store_manager.authentication.domain;

import com.jarabrama.store_manager.authentication.domain.model.SystemUser;
import java.util.Optional;
import java.util.UUID;

/**
 * SystemUserRepositoryPort
 */
public interface SystemUserRepositoryPort {
  Optional<SystemUser> findByUsername(String username);

  Optional<SystemUser> findById(UUID id);

  void save(SystemUser user);

  void update(UUID userId, SystemUser user);
}
