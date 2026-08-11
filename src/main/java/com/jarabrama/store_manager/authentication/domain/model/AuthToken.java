package com.jarabrama.store_manager.authentication.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Data
public class AuthToken {

  private  UUID id;
  private  String tokenHash;
  private  UUID userId;
  private  AuthTokenType tokenType;
  private Instant createdAt;
  private Instant expiresAt;
  private boolean revoked;

  public boolean isValid() {
    return !revoked && expiresAt.isAfter(Instant.now());
  }

}
