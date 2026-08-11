package com.jarabrama.store_manager.authentication.usecases;

import com.jarabrama.store_manager.authentication.domain.AuthTokenRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.SessionRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.exceptions.SessionException;
import com.jarabrama.store_manager.authentication.domain.model.SystemUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

/**
 * Use case that extends an active user session with a sliding expiration window.
 *
 * <p>On every authenticated request, the last session of the user is looked up and,
 * if it is still valid (not revoked and not expired), both the session and its
 * refresh token have their expiration pushed forward:
 * <ul>
 *   <li>Trusted device (session has a {@code trustedDeviceId}): expires 5 days from
 *       the moment of the request.</li>
 *   <li>Untrusted device: expires 15 minutes from the moment of the request.</li>
 * </ul>
 *
 * <p>The same refresh token is reused (no rotation) and the session's
 * {@code lastActivityAt} is updated to the moment of the request. If the user has
 * no session, or the last session is expired or revoked, a {@link SessionException}
 * is thrown so the caller can respond with HTTP 401 and prompt the user to log in
 * again.
 */
@Service
@RequiredArgsConstructor
public class ExtendSessionUseCase {

  private final static Duration TRUSTED_DEVICE_SESSION_EXTENSION = Duration.ofDays(5);
  private final static Duration UNTRUSTED_DEVICE_SESSION_EXTENSION = Duration.ofMinutes(15);

  private final SessionRepositoryPort sessionRepo;
  private final AuthTokenRepositoryPort authTokenRepo;

  /**
   * Extends the last active session of the given user and its refresh token.
   *
   * @param user the authenticated user whose session must be extended.
   * @throws SessionException if the user has no registered session, or the last
   *                          session or its refresh token is expired or revoked.
   */
  public void execute(SystemUser user) {
    var session = sessionRepo.findLastByUser(user).orElseThrow(() ->
            new SessionException("No se encontró ninguna sesión activa. Por favor, inicia sesión nuevamente"));

    if (!session.isValid())
      throw new SessionException("Tu sesión ha expirado. Por favor, inicia sesión nuevamente");

    var refreshToken = session.getRefreshToken();
    if (refreshToken == null || refreshToken.isRevoked())
      throw new SessionException("Tu sesión ha expirado. Por favor, inicia sesión nuevamente");

    var now = Instant.now();
    var extensionTimeout = session.getTrustedDeviceId() == null ?
            UNTRUSTED_DEVICE_SESSION_EXTENSION : TRUSTED_DEVICE_SESSION_EXTENSION;
    var newExpirationTimeout = now.plus(extensionTimeout);

    authTokenRepo.updateExpirationTimeout(refreshToken.getId(), newExpirationTimeout);
    sessionRepo.updateExpirationTimeout(session.getId(), newExpirationTimeout);
    sessionRepo.updateLastActivityAt(session.getId(), now);
  }
}
