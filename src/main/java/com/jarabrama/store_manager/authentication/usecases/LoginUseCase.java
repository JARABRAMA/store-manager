package com.jarabrama.store_manager.authentication.usecases;

import com.jarabrama.store_manager.authentication.domain.SessionRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.SystemUserRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.exceptions.InvalidCredentialsException;
import com.jarabrama.store_manager.authentication.presentation.dto.LoginRequest;
import com.jarabrama.store_manager.authentication.presentation.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUseCase {
  private final SystemUserRepositoryPort userRepo;
  private final PasswordEncoder passwordEncoder;
  private final SessionRepositoryPort sessionRepo;

  public LoginResponse execute(LoginRequest request) {
    var user = userRepo.findByUsername(request.username())
            .orElseThrow(() -> new InvalidCredentialsException("Credenciales incorrectas"));

    var correctPassword = passwordEncoder.matches(request.password(),
            user.getPasswordHash());

    if (!correctPassword) {
      throw new InvalidCredentialsException("Credenciales incorrectas");
    }

    sessionRepo.revokeAllByUser(user.getId());

    return null;
  }
}
