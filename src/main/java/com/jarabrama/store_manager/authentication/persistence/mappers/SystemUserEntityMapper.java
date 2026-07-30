package com.jarabrama.store_manager.authentication.persistence.mappers;

import com.jarabrama.store_manager.authentication.domain.model.SystemUser;
import com.jarabrama.store_manager.authentication.persistence.entities.SystemUserEntity;
import org.springframework.stereotype.Component;

@Component
public class SystemUserEntityMapper {

  public SystemUserEntity fromDomain(SystemUser domain) {
    return SystemUserEntity.builder()
            .id(domain.getId())
            .username(domain.getUsername())
            .role(domain.getRole())
            .passwordHash(domain.getPasswordHash())
            .build();
  }

  public SystemUser toDomain(SystemUserEntity entity) {
    return SystemUser.builder()
            .id(entity.getId())
            .username(entity.getUsername())
            .role(entity.getRole())
            .passwordHash(entity.getPasswordHash())
            .build();
  }
}
