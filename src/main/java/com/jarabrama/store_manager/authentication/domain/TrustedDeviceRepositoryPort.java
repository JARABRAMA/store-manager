package com.jarabrama.store_manager.authentication.domain;

import com.jarabrama.store_manager.authentication.domain.model.TrustedDevice;

import java.util.Optional;

public interface TrustedDeviceRepositoryPort {

  Optional<TrustedDevice> findByDeviceHash(String deviceHash);
  void saveNewTrustedDevice(TrustedDevice trustedDevice);
}
