package com.jarabrama.store_manager.authentication.persistence.mappers;

import com.jarabrama.store_manager.authentication.domain.model.SystemUser;
import com.jarabrama.store_manager.authentication.persistence.entities.SystemUserEntity;

public class SystemUserEntityMapper {

  public SystemUserEntity fromDomain(SystemUser domain) {
    return SystemUserEntity.builder()
            .id(domain.getId())
            .username(domain.getUsername())
            .role(domain.getRole())
            .passwordHash(domain.getPasswordHash())
            .build();
  }
}
