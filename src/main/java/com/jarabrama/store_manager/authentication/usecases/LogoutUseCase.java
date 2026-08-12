package com.jarabrama.store_manager.authentication.usecases;


import com.jarabrama.store_manager.authentication.domain.SessionRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.SystemUserRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.exceptions.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LogoutUseCase {
  private final SystemUserRepositoryPort userRepo;
  private final SessionRepositoryPort sessionRepo;

  public void execute(UUID userId) {
    var user = userRepo.findById(userId).orElseThrow(() ->
            new AuthException("No se pudo cerrar sesión, Usuario invalido"));

    sessionRepo.revokeAllByUser(user.getId());
  }
}
