package com.jarabrama.store_manager.authentication.usecases;

import com.jarabrama.store_manager.authentication.domain.TrustedDeviceRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.model.TrustedDevice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FindOrCreateTrustedDeviceUseCaseTest {

  @Mock
  private TrustedDeviceRepositoryPort trustedDeviceRepo;

  @InjectMocks
  private FindOrCreateTrustedDeviceUseCase findOrCreateTrustedDeviceUseCase;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  private TrustedDevice trustedDevice;

  private void buildTrustedDevice() {
    this.trustedDevice = TrustedDevice.builder()
            .userId(UUID.randomUUID())
            .deviceTokenHash("some hash")
            .lastUsedAt(Instant.now())
            .revoked(false)
            .id(UUID.randomUUID())
            .build();
  }

  @Test
  void when_repository_find_device_then_use_case_should_return_it() {
    buildTrustedDevice();

    when(trustedDeviceRepo.findByDeviceHashAndUserId(any(), any())).thenReturn(Optional.of(this.trustedDevice));

    var actual = findOrCreateTrustedDeviceUseCase.execute("hash", UUID.randomUUID());

    assertEquals(this.trustedDevice, actual);
    verify(trustedDeviceRepo).findByDeviceHashAndUserId(any(), any());
  }

  @Test
  void when_repository_did_not_find_device_then_use_case_should_create_new_device() {
    buildTrustedDevice();

    when(trustedDeviceRepo.findByDeviceHashAndUserId(any(), any())).thenReturn(Optional.empty());
    when(trustedDeviceRepo.save(any())).thenReturn(trustedDevice);

    var actual = findOrCreateTrustedDeviceUseCase.execute("hash", UUID.randomUUID());

    verify(trustedDeviceRepo).findByDeviceHashAndUserId(any(), any());
    verify(trustedDeviceRepo).save(any());

    assertEquals(this.trustedDevice, actual);
  }


}