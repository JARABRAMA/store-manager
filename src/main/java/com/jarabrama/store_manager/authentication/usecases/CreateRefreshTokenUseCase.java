package com.jarabrama.store_manager.authentication.usecases;

import com.jarabrama.store_manager.authentication.domain.AuthTokenRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.model.*;
import com.jarabrama.store_manager.authentication.service.JwtService;
import com.jarabrama.store_manager.authentication.service.model.NewJwtTokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Use case responsible for creating a refresh token for a given user.
 *
 * <p>A refresh token is created with an expiration time that depends on whether the request
 * comes from a trusted device:
 * <ul>
 *   <li>Trusted device: expires after 5 days.</li>
 *   <li>Untrusted device: expires after 15 minutes.</li>
 * </ul>
 *
 * <p>The generated JWT is persisted as an {@link AuthToken} through the
 * {@link AuthTokenRepositoryPort} so it can later be validated when refreshing a session.
 */
@Service
@RequiredArgsConstructor
public class CreateRefreshTokenUseCase {
  private final static Duration TRUSTED_DEVICE_EXPIRATION_TIMEOUT = Duration.ofDays(5);
  private final static Duration NOT_TRUSTED_DEVICE_EXPIRATION_TIMEOUT = Duration.ofMinutes(15);

  private final JwtService jwtService;
  private final AuthTokenRepositoryPort authTokenRepo;

  /**
   * Creates and persists a new refresh token for the given user.
   *
   * @param user           the user the token belongs to
   * @param trustedDeviceId the id of the trusted device, or {@code null} when the device is not trusted
   * @return the persisted {@link AuthToken} representing the refresh token
   */
  public AuthToken execute(SystemUser user, UUID trustedDeviceId) {
    var now = Instant.now();
    var token = AuthToken.builder()
            .userId(user.getId())
            .tokenType(AuthTokenType.REFRESH)
            .createdAt(now)
            .revoked(false)
            .build();

    if (trustedDeviceId != null) {
      token.setExpiresAt(now.plus(TRUSTED_DEVICE_EXPIRATION_TIMEOUT));
    } else {
      token.setExpiresAt(now.plus(NOT_TRUSTED_DEVICE_EXPIRATION_TIMEOUT));
    }

    token.setTokenHash(generateTokenHash(user, trustedDeviceId, token));

    return authTokenRepo.save(token);
  }

  /**
   * Builds the {@link NewJwtTokenRequest} for the refresh token and generates its JWT.
   *
   * @return the generated JWT string
   */
  private String generateTokenHash(SystemUser user, UUID trustedDeviceId,  AuthToken token) {
    var jwtTokenRequest = NewJwtTokenRequest.builder()
            .username(user.getUsername())
            .userRole(user.getRole())
            .trustedDeviceId(trustedDeviceId)
            .tokenType(AuthTokenType.REFRESH)
            .expirationTime(token.getExpiresAt())
            .build();
    return jwtService.generateToken(jwtTokenRequest);
  }
}
