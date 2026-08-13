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
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA-based implementation of the {@link AuthTokenRepositoryPort}.
 *
 * <p>Persists authentication tokens and delegates the update and revocation operations
 * to {@link AuthTokenJpaRepository}.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class AuthTokenRepositoryImpl implements AuthTokenRepositoryPort {
  private final AuthTokenJpaRepository repo;
  private final SystemUserJpaRepository userJpaRepo;
  private final AuthTokenEntityMapper mapper;

  /**
   * Persists the given token in the database.
   *
   * <p>The user the token belongs to must already exist in the system; otherwise a
   * {@link UserNotFoundException} is thrown.
   *
   * @param authToken the token to persist.
   * @return the saved token with its assigned id and persisted values.
   * @throws UserNotFoundException if the user referenced by the token does not exist.
   */
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

  /**
   * Updates the expiration time of the token identified by the given id.
   *
   * @param id the id of the token to update.
   * @param newExpirationTimeout the new expiration time for the token.
   */
  @Override
  @Transactional
  public void updateExpirationTimeout(UUID id, Instant newExpirationTimeout) {
    repo.updateExpiresAt(newExpirationTimeout, id);
  }

  /**
   * Marks all the tokens belonging to the given user as revoked.
   *
   * @param userId the id of the user whose tokens must be revoked.
   */
  @Override
  @Transactional
  public void revokeAllByUser(UUID userId) {
    repo.revokeAllByUser(userId);
  }
}
