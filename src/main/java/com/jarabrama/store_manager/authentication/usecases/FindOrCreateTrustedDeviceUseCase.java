package com.jarabrama.store_manager.authentication.usecases;

import com.jarabrama.store_manager.authentication.domain.TrustedDeviceRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.model.TrustedDevice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindOrCreateTrustedDeviceUseCase {
  private final TrustedDeviceRepositoryPort trustedDeviceRepo;

  public TrustedDevice execute(String trustedDeviceHash, UUID userId) {
    var device = trustedDeviceRepo.findByDeviceHashAndUserId(trustedDeviceHash, userId);
    if (device.isPresent()) return device.get();

    var newDevice = buildNewDevice(trustedDeviceHash, userId);
    return trustedDeviceRepo.save(newDevice);
  }

  private TrustedDevice buildNewDevice(String deviceHash, UUID userId) {
    var now = Instant.now();
    return TrustedDevice.builder()
            .userId(userId)
            .deviceTokenHash(deviceHash)
            .lastUsedAt(now)
            .revoked(false)
            .build();
  }
}
