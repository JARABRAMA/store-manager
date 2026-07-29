package com.jarabrama.store_manager.authentication.persistence.jpa;

import com.jarabrama.store_manager.authentication.persistence.entities.SystemUserEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * SystemUseJpaRepository
 */

public interface SystemUserJpaRepository
  extends JpaRepository<SystemUserEntity, UUID> {}
