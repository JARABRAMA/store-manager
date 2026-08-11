package com.jarabrama.store_manager.authentication.domain.model;

import com.jarabrama.store_manager.authentication.domain.exceptions.AuthTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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

  private  UUID id;
  private  String tokenHash;
  private  UUID userId;
  private  AuthTokenType tokenType;
  private Instant createdAt;
  private Instant expiresAt;
  private boolean revoked;

}
