package com.jarabrama.store_manager.authentication.domain.model;

import com.jarabrama.store_manager.authentication.domain.exceptions.AuthTokenException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Data
public class AuthToken {
  private static final Duration ACCESS_TOKEN_TIMEOUT = Duration.ofMinutes(5);
  private static final Duration NOT_TRUSTED_REFRESH_TOKEN_TIMEOUT = Duration.ofMinutes(15);
  private static final Duration TRUSTED_REFRESH_TOKEN_TIMEOUT = Duration.ofDays(5);

  private final UUID id;
  private final String tokenHash;
  private final UUID userId;
  private final AuthTokenType tokenType;
  private Instant createdAt;
  private Instant expiresAt;
  private boolean revoked;


}
