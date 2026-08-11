package com.jarabrama.store_manager.authentication.domain;

import com.jarabrama.store_manager.authentication.domain.model.TrustedDevice;

import java.util.Optional;
import java.util.UUID;

public interface TrustedDeviceRepositoryPort {

  Optional<TrustedDevice> findByDeviceHashAndUserId(String deviceHash, UUID userId);
  TrustedDevice save(TrustedDevice trustedDevice);
}
