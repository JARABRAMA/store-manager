package com.jarabrama.store_manager.authentication.persistence;

import com.jarabrama.store_manager.authentication.domain.SystemUserRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.exceptions.DatabaseException;
import com.jarabrama.store_manager.authentication.domain.model.SystemUser;
import com.jarabrama.store_manager.authentication.persistence.jpa.SystemUserJpaRepository;

import com.jarabrama.store_manager.authentication.persistence.mappers.SystemUserEntityMapper;
import lombok.AllArgsConstructor;

import java.util.Optional;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@AllArgsConstructor
@Repository
@Slf4j
public class SystemUserRepositoryImpl implements SystemUserRepositoryPort {

  private final SystemUserJpaRepository jpaRepository;
  private final SystemUserEntityMapper mapper;

  @Override
  public Optional<SystemUser> findByUsername(String username) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException(
            "Unimplemented method 'findByUsername'"
    );
  }

  @Override
  public Optional<SystemUser> findById(UUID id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findById'");
  }

  @Override
  public void save(SystemUser user) {
    var entity = mapper.fromDomain(user);
    try {
      jpaRepository.save(entity);
    } catch (RuntimeException e) {
      log.error("user repository - save: {}", e.getMessage());
      throw new DatabaseException("Error de base de datos");
    }
  }

  @Override
  public void update(UUID userId, SystemUser user) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'update'");
  }
}
