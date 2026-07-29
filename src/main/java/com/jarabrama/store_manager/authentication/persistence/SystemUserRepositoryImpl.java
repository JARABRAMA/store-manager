package com.jarabrama.store_manager.authentication.persistence;

import com.jarabrama.store_manager.authentication.domain.SystemUserRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.model.SystemUser;
import com.jarabrama.store_manager.authentication.persistence.jpa.SystemUserJpaRepository;

import lombok.AllArgsConstructor;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

@AllArgsConstructor
@Repository
public class SystemUserRepositoryImpl implements SystemUserRepositoryPort {

  private final SystemUserJpaRepository jpaRepository;

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
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'save'");
  }

  @Override
  public void update(UUID userId, SystemUser user) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'update'");
  }
}
