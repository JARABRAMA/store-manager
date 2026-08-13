package com.jarabrama.store_manager.authentication.persistence;

import com.jarabrama.store_manager.authentication.domain.AuthTokenRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.exceptions.UserNotFoundException;
import com.jarabrama.store_manager.authentication.domain.model.AuthToken;
import com.jarabrama.store_manager.authentication.persistence.jpa.AuthTokenJpaRepository;
import com.jarabrama.store_manager.authentication.persistence.jpa.SystemUserJpaRepository;
import com.jarabrama.store_manager.authentication.persistence.mappers.AuthTokenEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AuthTokenRepositoryImpl implements AuthTokenRepositoryPort {
  private final AuthTokenJpaRepository repo;
  private final SystemUserJpaRepository userJpaRepo;
  private final AuthTokenEntityMapper mapper;

  @Override
  public AuthToken save(AuthToken authToken) {
    var user = userJpaRepo.findById(authToken.getUserId()).orElseThrow(() ->
            new UserNotFoundException("Usuario no encontrado"));

    var entity = mapper.toEntity(authToken);
    entity.setUser(user);

    var savedEntity = repo.save(entity);
    log.info("saved entity: {}", savedEntity);
    return mapper.toDomain(savedEntity);
  }

  @Override
  public void updateExpirationTimeout(UUID id, Instant newExpirationTimeout) {

  }

  @Override
  public void revokeAllByUser(UUID userId) {

  }
}
