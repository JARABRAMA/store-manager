package com.jarabrama.store_manager.authentication.usecases;

import com.jarabrama.store_manager.authentication.domain.TrustedDeviceRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.model.TrustedDevice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindOrCreateTrustedDeviceUseCase {
  private final TrustedDeviceRepositoryPort trustedDeviceRepositoryPort;

  public TrustedDevice execute(String trustedDeviceHash) {

  }
}
