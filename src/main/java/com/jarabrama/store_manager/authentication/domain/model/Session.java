package com.jarabrama.store_manager.authentication.domain.model;

import com.jarabrama.store_manager.authentication.domain.exceptions.SessionException;
import lombok.*;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Data
public class Session {
  private static final Duration TRUSTED_TIMEOUT = Duration.ofDays(5);
  private static final Duration UNTRUSTED_TIMEOUT = Duration.ofMinutes(15);

  private final UUID id;
  private final UUID userId;
  private final UUID trustedDeviceId;
  private Instant lastActivityAt;
  private Instant expiresAt;
  private boolean revoked;
  private AuthToken authToken;


}
