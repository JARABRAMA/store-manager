package com.jarabrama.store_manager.authentication.usecases;

import com.jarabrama.store_manager.authentication.domain.SessionRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.model.AuthToken;
import com.jarabrama.store_manager.authentication.domain.model.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.UUID;

/**
 * Use case responsible for creating a new session and persisting it.
 *
 * <p>The session is built from the user, an optional trusted device and the refresh token
 * issued during login, and is stored through the {@link SessionRepositoryPort}.
 * Its activity and expiration times are derived from the refresh token's timestamps.
 */
@Service
@RequiredArgsConstructor
public class CreateAndSaveNewSessionUseCase {
  private final SessionRepositoryPort sessionRepo;

  /**
   * Creates and persists a new session for the given user.
   *
   * @param userId          the id of the user the session belongs to
   * @param trustedDeviceId the id of the trusted device, or {@code null} when the device is not trusted
   * @param refreshToken    the refresh token issued for the session
   * @return the persisted {@link Session}
   */
  public Session execute(UUID userId, UUID trustedDeviceId, AuthToken refreshToken) {
    var newSession = Session.builder()
            .userId(userId)
            .trustedDeviceId(trustedDeviceId)
            .lastActivityAt(refreshToken.getCreatedAt())
            .expiresAt(refreshToken.getExpiresAt())
            .revoked(false)
            .refreshToken(refreshToken)
            .build();

    return sessionRepo.save(newSession);
  }
}
