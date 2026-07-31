package com.jarabrama.store_manager.authentication.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;


@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TrustedDevice {
  private final UUID id;
  private final UUID userId;
  private final String deviceTokenHash;
  private Instant lastUsedAt;
  private boolean revoked;

}
