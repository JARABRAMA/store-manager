package com.jarabrama.store_manager.authentication.domain.model;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Data
public class Session {
  private final UUID id;
  private final UUID userId;
  private final UUID trustedDeviceId;
  private Instant lastActivityAt;
  private Instant expiresAt;
  private boolean revoked;
  private AuthToken refreshToken;

  public boolean isValid() {
    return !isRevoked() && expiresAt != null && expiresAt.isAfter(Instant.now());
  }
}
